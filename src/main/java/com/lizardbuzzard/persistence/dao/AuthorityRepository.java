package com.lizardbuzzard.persistence.dao;

import com.lizardbuzzard.persistence.model.AuthorityEntity;
import com.lizardbuzzard.persistence.model.AuthorityId;
import com.lizardbuzzard.persistence.model.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AuthorityRepository extends JpaRepository<AuthorityEntity, AuthorityId> {
    List<AuthorityEntity> findByUsername(String username);

    List<AuthorityEntity> findByUser(UserEntity user);
}
