package com.b209.hansotbab.wishlist.repository;

import com.b209.hansotbab.fridge.entity.Fridge;
import com.b209.hansotbab.fridge.repository.FridgeRepository;
import com.b209.hansotbab.user.repository.UserRepository;
import com.b209.hansotbab.user.entity.User;
import com.b209.hansotbab.wishlist.entity.Wishlist;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class WishlistRepositoryTest {

    @Autowired
    WishlistRepository wishlistRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    FridgeRepository fridgeRepository;

    User admin1, admin2, user;
    Fridge fridge;
    Wishlist wishlist;
    Wishlist deleteWishlist;

    @BeforeEach
    void setUp() {
        admin1 = User.builder()
                .email("admin@gmail.com")
                .nickname("관리자1")
                .build();

        admin2 = User.builder()
                .email("admin2@gmail.com")
                .nickname("관리자2")
                .build();

        user = User.builder()
                .email("user@gmail.com")
                .nickname("유저1")
                .build();

        userRepository.save(admin1);
        userRepository.save(admin2);
        userRepository.save(user);

        ArrayList<String> admins = new ArrayList<>();
        admins.add(admin1.getUuid().toString());
        admins.add(admin2.getUuid().toString());

        fridge = Fridge.builder()
                .fridgeNumber(1)
                .fridgeLocationName("수원시 가동")
                .fridgeLocationAddress("324-4")
                .fridgeAdmins(admins)
                .build();
        fridgeRepository.save(fridge);

        wishlist = Wishlist.builder()
                .wishlistContent("양배추가 필요합니다.")
                .fridge(fridge)
                .user(user)
                .build();
        wishlistRepository.save(wishlist);

        deleteWishlist = Wishlist.builder()
                .wishlistContent("양배추가 필요했었습니다.")
                .fridge(fridge)
                .user(user)
                .build();

        deleteWishlist.updateDelete();
        wishlistRepository.save(wishlist);
        wishlistRepository.save(deleteWishlist);
        wishlist = wishlistRepository.getReferenceById(1L);
        deleteWishlist = wishlistRepository.getReferenceById(2L);
    }

    @AfterEach
    void tearDown() {
        wishlist = wishlistRepository.getReferenceById(1L);
        wishlistRepository.delete(wishlist);
        deleteWishlist = wishlistRepository.getReferenceById(2L);
        wishlistRepository.delete(deleteWishlist);
        fridge = fridgeRepository.getReferenceById(1L);
        fridgeRepository.delete(fridge);
        admin1 = userRepository.getReferenceById(UUID.randomUUID());
        userRepository.delete(admin1);
        user = userRepository.getReferenceById(UUID.randomUUID());
        userRepository.delete(user);
    }

    @Test
    void findAllByFridge() {
        // when
        List<Wishlist> wishlists = wishlistRepository.findAllByFridgeAndIsDeleteIsFalse(fridge);

        // then
        assertEquals(1, wishlists.size());
    }

    @Test
    void 삭제여부기반위시리스트조회() {
        // when
        Optional<Wishlist> wishlist = wishlistRepository.findByWishlistIdAndIsDeleteFalse(1L);
        Optional<Wishlist> deleteWishlist = wishlistRepository.findByWishlistIdAndIsDeleteFalse(2L);

        // then
        assertThat(wishlist).isNotEmpty();
        assertThat(deleteWishlist).isEmpty();
    }
}