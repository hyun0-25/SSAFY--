package com.b209.hansotbab.user.repository;

import com.b209.hansotbab.user.entity.RoleType;
import com.b209.hansotbab.user.entity.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
@SpringBootTest
class UserRepositoryTest {

    @Autowired
    UserRepository userRepository;

    User falseUser;
    User trueUser;

    @BeforeEach
    void setUp() {
    falseUser = User.builder()
            .email("admin@gmail.com")
            .nickname("현재회원")
            .roleType(RoleType.USER)
            .build();

    trueUser = User.builder()
            .email("user@gmail.com")
            .nickname("탈퇴한회원")
            .roleType(RoleType.USER)
            .build();

    trueUser.updateDelete();
    userRepository.save(falseUser);
    userRepository.save(trueUser);
    }

    @AfterEach
    void tearDown() {
        falseUser = userRepository.getReferenceById(UUID.randomUUID());
        userRepository.delete(falseUser);
        trueUser = userRepository.getReferenceById(UUID.randomUUID());
        userRepository.delete(trueUser);
    }

    @Test
    void 탈퇴여부기반회원조회(){
        // when
        Optional<User> falseUser = userRepository.findByUuidAndIsDeleteFalse(UUID.randomUUID());
        Optional<User> trueUser = userRepository.findByUuidAndIsDeleteFalse(UUID.randomUUID());

        // then
        assertThat(falseUser).isNotEmpty();
        assertThat(trueUser).isEmpty();
    }
}