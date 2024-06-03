package com.shopmax.service;

import com.shopmax.dto.ItemFormDto;
import com.shopmax.dto.ItemImgDto;
import com.shopmax.dto.ItemSearchDto;
import com.shopmax.dto.MainItemDto;
import com.shopmax.entity.Item;
import com.shopmax.entity.ItemImg;
import com.shopmax.repository.ItemImgRepository;
import com.shopmax.repository.ItemRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ItemService {
    private final ItemRepository itemRepository;
    private final ItemImgRepository itemImgRepository;
    private final ItemImgService itemImgService;


    //item 테이블에 상품등록(insert)
    public Long saveItem(ItemFormDto itemFormDto,
                         List<MultipartFile> itemImgFileList) throws Exception {
        
        // 1. 상품 등록(insert)
        Item item = itemFormDto.createItem(); // dto -> entity
        itemRepository.save(item); // insert
        
        // 2. 이미지 등록(총 5개의 이미지를 등록해야 하므로 for구문으로 등록)
        for (int i = 0; i < itemImgFileList.size(); i++) {
            ItemImg itemImg = new ItemImg();
            itemImg.setItem(item); // ★itemImg가 item을 참조하므로 반드시 item 객체를 입력해준다.
            
            //첫번째 이미지를 대표 이미지로 지정
            if(i == 0) {
                itemImg.setRepImgYn("Y");
            } else {
                itemImg.setRepImgYn("N");
            }
            //이미지 파일을 하나씩 저장
            itemImgService.saveItemImg(itemImg, itemImgFileList.get(i));
        }


        return itemFormDto.getId();
    }

    //상품 가져오기
    @Transactional(readOnly = true)
    public ItemFormDto getItemDtl(Long itemId) {
        //1.item_img 테이블의 이미지를 가져온다.
        List<ItemImg> itemImgList = itemImgRepository.findByItemIdOrderByIdAsc(itemId);

        //ItemImg Entity -> Dto 변환
        List<ItemImgDto> itemImgDtoList = new ArrayList<>();
        for(ItemImg itemImg : itemImgList) {
            ItemImgDto itemImgDto = ItemImgDto.of(itemImg); // dto객체로 변환
            itemImgDtoList.add(itemImgDto);
        }

        //2. item 테이블에 있는 데이터를 가져온다.
        Item item = itemRepository.findById(itemId).orElseThrow(EntityNotFoundException::new);

        //Item Entity -> Dto 변환
        ItemFormDto itemFormDto = ItemFormDto.of(item);

        //3. ItemFormDto에 itemImgDtoList를 넣어준다.
        itemFormDto.setItemImgDtoList(itemImgDtoList);

        return itemFormDto;
    }

    //상품 수정하기
    public Long updateItem(ItemFormDto itemFormDto, List<MultipartFile> itemImgFileList) throws Exception {
        //1. item 엔티티 수정
        //★update를 진행하기 전 반드시 select를 해온다.
        // -> 영속성 컨텍스트에 item 엔티티가 없다면 가져오기 위해서
        Item item = itemRepository.findById(itemFormDto.getId()).orElseThrow(EntityNotFoundException::new);

        //update 실행
        //★ 한번 select 를 진행하면 엔티티가 영속성 컨텍스트에 저장되고
        // 변경감지 기능으로 인해 트랜잭션이 커밋 시점에 엔티티와 DB에 저장된 값이
        // 다른 내용을 jPA에서 update해준다.
        item.updateItem(itemFormDto);

        // 2. item_img 엔티티 수정
        List<Long> itemImgIds = itemFormDto.getItemImgIds(); // 상품 이미지 아이디 리스트 조회

        for (int i = 0; i < itemImgFileList.size(); i++) {
            //itemImgService.updateItemImg(itemImg Id, 이미지 파일);
            itemImgService.updateItemImg(itemImgIds.get(i), itemImgFileList.get(i));
        }

        return item.getId(); // 변경한 item의 id리턴
    }

    //
    @Transactional(readOnly = true)
    public Page<Item> getAdminImplPage(ItemSearchDto itemSearchDto, Pageable pageable) {
        Page<Item> itemPage = itemRepository.getAdminItemPage(itemSearchDto, pageable);
        return itemPage;
    }

    public Page<MainItemDto> getMainItemPage(ItemSearchDto itemSearchDto, Pageable pageable) {
        Page<MainItemDto> mainItemPage =
                itemRepository.getMainItemPage(itemSearchDto, pageable);
        return mainItemPage;
    }
}
