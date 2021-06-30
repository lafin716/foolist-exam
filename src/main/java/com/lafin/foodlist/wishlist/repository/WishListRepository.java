package com.lafin.foodlist.wishlist.repository;

import com.lafin.foodlist.db.AbstractMemoryDBRepository;
import com.lafin.foodlist.db.MemoryDBRepository;
import com.lafin.foodlist.wishlist.entity.WIshListEntity;
import org.springframework.stereotype.Repository;

@Repository
public class WishListRepository extends AbstractMemoryDBRepository<WIshListEntity> {
}
