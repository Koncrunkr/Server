package ru.comgrid.server.service.file;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.comgrid.server.controller.util.Image;
import ru.comgrid.server.controller.util.ImageEntity;
import ru.comgrid.server.exception.ImageDoesNotExistException;
import ru.comgrid.server.exception.ImageEmptyParamException;
import ru.comgrid.server.exception.ImageWriteToFileException;
import ru.comgrid.server.exception.InvalidLinkException;
import ru.comgrid.server.model.FileType;
import ru.comgrid.server.model.InnerFile;
import ru.comgrid.server.repository.InnerFileRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.springframework.util.StringUtils.getFilenameExtension;

@Service
public class FileService{
    public final String fileRoute;
    private final Set<String> allowedExtensions;
    private final InnerFileRepository innerFileRepository;
    private final ImageService imageService;

    public FileService(
        @Value("${ru.comgrid.images.fileRoute}") String fileRoute,
        @Value("${ru.comgrid.images.allowedExtensions}") List<String> allowedExtensions,
        @Autowired ImageService imageService,
        @Autowired InnerFileRepository innerFileRepository
    ) throws IOException{
        this.fileRoute = fileRoute;
        this.allowedExtensions = new HashSet<>(allowedExtensions);
        this.innerFileRepository = innerFileRepository;
        this.imageService = imageService;
        Files.createDirectories(Path.of(fileRoute));
    }

    @NotNull
    public ImageEntity uploadImage(Image file){
        ImageEntity imageEntity = uploadImage(file.getFile(), null);
        var innerFile = new InnerFile(file.getName(), imageEntity.getUrl(), FileType.IMAGE);
        try{
            innerFileRepository.save(innerFile);
        }catch(RuntimeException e){
            deleteFile(imageEntity.getUrl());
        }
        return imageEntity;
    }

    /**
     * Uploads file it is not yet uploaded and returns link to it.
     * Either file or fileLink has to be specified.
     *
     * @param file     uploaded file or null
     * @param fileLink link to file or null
     * @return relative link to file
     */
    public ImageEntity uploadImage(@Nullable MultipartFile file, @Nullable String fileLink){
        String imagePath = getNewImageUrl();
        try{
            if(file == null || file.isEmpty()){
                return getImageFromLink(fileLink, imagePath);
            }
            byte[] bytes = imageService.compressImage(file.getBytes());
            Files.write(Path.of(imagePath), bytes);
            return new ImageEntity(imagePath);
        }catch(IOException e){
            throw new ImageWriteToFileException();
        }
    }

    private void deleteFile(String fileLink){
        try{
            Files.delete(Path.of(fileLink));
        }catch(IOException e){
            throw new RuntimeException(e);
        }
    }

    private String getNewImageUrl(){
        return fileRoute + UUID.randomUUID() + ".webp";
    }

    @NotNull
    private ImageEntity getImageFromLink(String fileLink, String imagePath) throws IOException{
        if(fileLink == null){
            throw new ImageEmptyParamException();
        }
        if(fileLink.startsWith("/images/")){
            checkFileExists(fileLink);
            return new ImageEntity(fileLink);
        }
        byte[] bytes = imageService.compressImage(fileLink);
        Files.write(Path.of(imagePath), bytes);
        return new ImageEntity(imagePath);
    }

    private void checkFileExists(String fileLink){
        if(!Files.exists(Path.of(fileLink), LinkOption.NOFOLLOW_LINKS))
            throw new ImageDoesNotExistException(fileLink);
    }

    @NotNull
    public ResponseEntity<byte[]> getFile(String fileLink){
        try{
            String filenameExtension = getFilenameExtension(fileLink);
            if(filenameExtension == null)
                throw new InvalidLinkException(fileLink + ", no extension");
            if(!allowedExtensions.contains(filenameExtension))
                throw new InvalidLinkException(fileLink + ", bad extension");
            //check whether it's legal uuid string
            UUID.fromString(fileLink.substring(fileRoute.length(), fileLink.length() - filenameExtension.length() - 1));
            return ResponseEntity
                .ok()
                .contentType(new MediaType("image", filenameExtension))
                .body(Files.readAllBytes(Path.of(fileLink)));
        }catch(IllegalArgumentException | NullPointerException | IOException | StringIndexOutOfBoundsException e){
            throw new InvalidLinkException(fileLink, e);
        }
    }
}
