package com.b209.hansotbab.fridge.repository;

import com.b209.hansotbab.fridge.entity.Fridge;
import com.b209.hansotbab.user.entity.RoleType;
import com.b209.hansotbab.user.entity.User;
import com.b209.hansotbab.user.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class FridgeRepositoryTest {

    @Autowired
    UserRepository userRepository;
    @Autowired
    FridgeRepository fridgeRepository;

    User admin1, admin2;
    Fridge falseFridge;
    Fridge trueFridge;

    @BeforeEach
    void setUp() {
        admin1 = User.builder()
                .email("admin@gmail.com")
                .nickname("관리자1")
                .roleType(RoleType.USER)
                .build();

        userRepository.save(admin1);

        admin2 = User.builder()
                .email("admin2@gmail.com")
                .nickname("관리자2")
                .roleType(RoleType.USER)
                .build();

        userRepository.save(admin2);

        ArrayList<String> admins = new ArrayList<>();
        admins.add(admin1.getUuid().toString());
        admins.add(admin2.getUuid().toString());

        falseFridge = Fridge.builder()
                .fridgeNumber(1)
                .fridgeLocationName("수원시 가동")
                .fridgeLocationAddress("324-4")
                .fridgeAdmins(admins)
                .build();
        trueFridge = Fridge.builder()
                .fridgeNumber(2)
                .fridgeLocationName("수원시 나동")
                .fridgeLocationAddress("324-4")
                .fridgeAdmins(admins)
                .build();
        trueFridge.updateDelete();
        fridgeRepository.save(falseFridge);
        fridgeRepository.save(trueFridge);
        falseFridge = fridgeRepository.getReferenceById(1L);
        trueFridge = fridgeRepository.getReferenceById(2L);
    }
    @AfterEach
    void tearDown() {
        falseFridge = fridgeRepository.getReferenceById(1L);
        fridgeRepository.delete(falseFridge);
        trueFridge = fridgeRepository.getReferenceById(2L);
        fridgeRepository.delete(trueFridge);
        admin1 = userRepository.getReferenceById(UUID.randomUUID());
        userRepository.delete(admin1);
    }

    @Test
    void 삭제여부기반냉장고조회() {
        // when
        Optional<Fridge> falseFridge = fridgeRepository.findByFridgeIdAndIsDeleteIsFalse(1L);
        Optional<Fridge> trueFridge = fridgeRepository.findByFridgeIdAndIsDeleteIsFalse(2L);

        // then
        assertThat(falseFridge).isNotEmpty();
        assertThat(trueFridge).isEmpty();
    }
}