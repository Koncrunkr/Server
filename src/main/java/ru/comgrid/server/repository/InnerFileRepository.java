package ru.comgrid.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.comgrid.server.model.InnerFile;

@Repository
public interface InnerFileRepository extends JpaRepository<InnerFile, Long>{
}
