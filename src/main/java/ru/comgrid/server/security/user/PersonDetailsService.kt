package ru.comgrid.server.security.user

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service
import ru.comgrid.server.repository.PersonRepository
import ru.comgrid.server.security.user.info.UserPrincipal
import java.math.BigDecimal

@Service
class PersonDetailsService(
    @param:Autowired private val personRepository: PersonRepository,
) : UserDetailsService {
    @Throws(UsernameNotFoundException::class)
    override fun loadUserByUsername(username: String): UserDetails {
        return personRepository
            .findById(BigDecimal(username))
            .map { person -> UserPrincipal.create(person) }
            .orElseThrow { UsernameNotFoundException(username) }
    }
}