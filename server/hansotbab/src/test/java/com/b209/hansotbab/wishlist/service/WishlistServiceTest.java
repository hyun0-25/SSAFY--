package com.b209.hansotbab.wishlist.service;

import com.b209.hansotbab.fridge.entity.Fridge;
import com.b209.hansotbab.fridge.repository.FridgeRepository;
import com.b209.hansotbab.user.entity.User;
import com.b209.hansotbab.user.repository.UserRepository;
import com.b209.hansotbab.wishlist.dto.request.WishlistRequestDTO;
import com.b209.hansotbab.wishlist.dto.response.WishlistResponseDTO;
import com.b209.hansotbab.wishlist.entity.Wishlist;
import com.b209.hansotbab.wishlist.repository.WishlistRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class WishlistServiceTest {

    @Mock
    WishlistRepository wishlistRepository;
    @Mock
    FridgeRepository fridgeRepository;
    @Mock
    UserRepository userRepository;
    @InjectMocks
    WishlistServiceImpl wishlistServiceImpl;

    User user;
    Fridge fridge;
    Wishlist wishlist;

    @BeforeEach
    void setUp() {
        ArrayList<String> admins = new ArrayList<>();
        admins.add(UUID.randomUUID().toString());

        fridge = Fridge.builder()
                .fridgeId(1L)
                .fridgeNumber(1)
                .fridgeLocationName("수원시 가동")
                .fridgeLocationAddress("324-4")
                .fridgeAdmins(admins)
                .build();

        user = User.builder()
                .email("user@gmail.com")
                .nickname("유저11")
                .build();

        wishlist = Wishlist.builder()
                .wishlistId(1L)
                .wishlistContent("양파주세요")
                .user(user)
                .fridge(fridge).build();
    }

    @Test
    void findAllWishlist() {
        // given
        List<Wishlist> wishlists = List.of(wishlist);
        given(fridgeRepository.findByFridgeIdAndIsDeleteIsFalse(any(Long.class))).willReturn(Optional.of(fridge));
        given(wishlistRepository.findAllByFridgeAndIsDeleteIsFalse(fridge)).willReturn(wishlists);
        UUID uuid = UUID.randomUUID();
        // when
        List<WishlistResponseDTO> wishlistList = wishlistServiceImpl.findAllWishlist(fridge.getFridgeId(), uuid);

        // then
        assertThat(wishlistList.size()).isEqualTo(1);
    }

    @Test
    void validateWishlistRequiredFields() {
        // given
        WishlistRequestDTO wishlistRequestDTO = WishlistRequestDTO.builder()
                .build();

        // when
        IllegalArgumentException e = Assertions.assertThrows(IllegalArgumentException.class,
                () -> wishlistServiceImpl.validateWishlistRequiredFields(wishlistRequestDTO));

        // then
        assertThat(e.getMessage()).isEqualTo("Wishlist 내용은 필수입니다.");
    }


    @Test
    void findWishlistInfo() {
        // given
        given(wishlistRepository.findByWishlistIdAndIsDeleteFalse(any(Long.class))).willReturn(Optional.of(wishlist));
        UUID uuid = UUID.randomUUID();
        // when
        WishlistResponseDTO wishlistResponseDTO = wishlistServiceImpl.findWishlistInfo(any(Long.class), uuid);

        // then
        assertThat(wishlistResponseDTO.getWishlistId()).as("wishlistId 값 생성").isEqualTo(1L);
        assertThat(wishlistResponseDTO.getFridgeId()).as("fridgeId 값 확인").isEqualTo(0L);
        assertThat(wishlistResponseDTO.getUserNickname()).as("userId 값 확인").isEqualTo("유저1");
        assertThat(wishlistResponseDTO.getWishlistContent()).as("wishlistContent 값 확인").isEqualTo("양파주세요");
    }

    @Test
    void createWishlist() {
        // given
        WishlistRequestDTO wishlistRequestDTO = WishlistRequestDTO.builder()
                .wishlistContent("양배추가 필요해요2")
                .build();
        given(fridgeRepository.findByFridgeIdAndIsDeleteIsFalse(any(Long.class))).willReturn(Optional.of(fridge));
        given(userRepository.findByUuidAndIsDeleteFalse(any(UUID.class))).willReturn(Optional.of(user));
        given(wishlistRepository.save(any(Wishlist.class))).willReturn(any(Wishlist.class));
        UUID uuid = UUID.randomUUID();
        // when, then
        assertDoesNotThrow(() -> wishlistServiceImpl.createWishlist(fridge.getFridgeId(), wishlistRequestDTO, uuid));
    }

    @Test
    void updateWishlist() {
        // given
        WishlistRequestDTO wishlistRequestDTO = WishlistRequestDTO.builder()
                .wishlistContent("양배추가 필요해요2")
                .build();

        given(wishlistRepository.findByWishlistIdAndIsDeleteFalse(any(Long.class))).willReturn(Optional.of(wishlist));
        given(userRepository.findByUuidAndIsDeleteFalse(any(UUID.class))).willReturn(Optional.of(user));
        UUID uuid = user.getUuid();

        // when, then
        assertDoesNotThrow(() -> wishlistServiceImpl.updateWishlist(wishlist.getWishlistId(), wishlistRequestDTO, uuid));
    }

    @Test
    void deleteWishlist() {
        // given
        given(wishlistRepository.findByWishlistIdAndIsDeleteFalse(any(Long.class))).willReturn(Optional.of(wishlist));
        UUID uuid = UUID.randomUUID();
        // when, then
        assertDoesNotThrow(() -> wishlistServiceImpl.deleteWishlist(wishlist.getWishlistId(), uuid));

    }
}