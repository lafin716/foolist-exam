package com.lafin.foodlist.wishlist.service;

import com.lafin.foodlist.naver.NaverClient;
import com.lafin.foodlist.naver.dto.SearchImageReq;
import com.lafin.foodlist.naver.dto.SearchLocalReq;
import com.lafin.foodlist.wishlist.dto.WishListDto;
import com.lafin.foodlist.wishlist.entity.WishListEntity;
import com.lafin.foodlist.wishlist.repository.WishListRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WishListService {

    private final NaverClient naverClient;
    private final WishListRepository wishListRepository;

    public WishListDto search(String query) {

        // 지역 검색
        var searchLocalReq = new SearchLocalReq();
        searchLocalReq.setQuery(query);

        var searchLocalResp = naverClient.searchLocal(searchLocalReq);
        if (searchLocalResp.getTotal() > 0) {
            var localItem = searchLocalResp.getItems().stream().findFirst().get();

            var imageQuery = localItem.getTitle().replaceAll("<[^>]*>", "");
            var searchImageReq = new SearchImageReq();
            searchImageReq.setQuery(imageQuery);

            // 이미지 검색
            var searchImageResp = naverClient.searchImage(searchImageReq);

            if (searchImageResp.getTotal() > 0) {
                var imageItem = searchImageResp.getItems().stream().findFirst().get();
                // 결과 만들기
                var result = new WishListDto();
                result.setTitle(localItem.getTitle());
                result.setCategory(localItem.getCategory());
                result.setAddress(localItem.getAddress());
                result.setRoadAddress(localItem.getRoadAddress());
                result.setHomePageLink(localItem.getLink());
                result.setImageLink(imageItem.getLink());

                return result;
            }
        }

        return new WishListDto();
    }

    public WishListDto add(WishListDto wishListDto) {
        var entity = dtoToEntity(wishListDto);
        var saveEntity = wishListRepository.save(entity);
        return entityToDto(saveEntity);
    }

    public List<WishListDto> findAll() {
        return wishListRepository.listAll()
                .stream()
                .map(it -> entityToDto(it))
                .collect(Collectors.toList());
    }

    private WishListDto entityToDto(WishListEntity wishListEntity) {
        var dto = new WishListDto();

        dto.setIndex(wishListEntity.getIndex());
        dto.setTitle(wishListEntity.getTitle());
        dto.setCategory(wishListEntity.getCategory());
        dto.setAddress(wishListEntity.getAddress());
        dto.setRoadAddress(wishListEntity.getRoadAddress());
        dto.setHomePageLink(wishListEntity.getHomePageLink());
        dto.setImageLink(wishListEntity.getImageLink());
        dto.setVisit(wishListEntity.isVisit());
        dto.setVisitCount(wishListEntity.getVisitCount());
        dto.setLastVisitDate(wishListEntity.getLastVisitDate());

        return dto;
    }

    private WishListEntity dtoToEntity(WishListDto wishListDto) {
        var entity = new WishListEntity();

        entity.setIndex(wishListDto.getIndex());
        entity.setTitle(wishListDto.getTitle());
        entity.setCategory(wishListDto.getCategory());
        entity.setAddress(wishListDto.getAddress());
        entity.setRoadAddress(wishListDto.getRoadAddress());
        entity.setHomePageLink(wishListDto.getHomePageLink());
        entity.setImageLink(wishListDto.getImageLink());
        entity.setVisit(wishListDto.isVisit());
        entity.setVisitCount(wishListDto.getVisitCount());
        entity.setLastVisitDate(wishListDto.getLastVisitDate());

        return entity;
    }

    public void delete(int index) {
        wishListRepository.deleteById(index);
    }

    public void addVisit(int index) {
        var wishItem = wishListRepository.findById(index);
        if (wishItem.isPresent()) {
            var item = wishItem.get();

            item.setVisit(true);
            item.setVisitCount(item.getVisitCount() + 1);
        }
    }
}
