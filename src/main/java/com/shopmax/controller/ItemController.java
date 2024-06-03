package com.shopmax.controller;

import com.shopmax.dto.ItemFormDto;
import com.shopmax.dto.ItemSearchDto;
import com.shopmax.dto.MainItemDto;
import com.shopmax.entity.Item;
import com.shopmax.service.ItemService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    //상품 등록 페이지 보여줌
    @GetMapping(value = "/admin/item/new")
    public String itemForm(Model model) {
        model.addAttribute("itemFormDto", new ItemFormDto()); // itemFormDto로 validation체크
        return "item/itemForm";
    }

    //상품 등록 처리(insert)
    @PostMapping(value = "/admin/item/new")
    public String itemNew(@Valid ItemFormDto itemFormDto, BindingResult bindingResult,
                          //RequestParam: input 태그의 name, 이미지를 5개 받아오므로 List로 받는다
                          Model model, @RequestParam("itemImgFile") List<MultipartFile> itemImgFileList) {
        if(bindingResult.hasErrors()) return "item/itemForm"; // 유효성 체크에서 걸리면 다시 돌아간다.

        //상품등록 전에 첫번째 이미지가 있는지 없는지 검사(첫번째 이미지는 필수 입력값)
        if(itemImgFileList.get(0).isEmpty()) {
            model.addAttribute("errorMessage", "첫번째 상품 이미지는 필수 입력입니다");
            return "item/itemForm";
        }

        try {
            itemService.saveItem(itemFormDto, itemImgFileList);
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("errorMessage"
                    , "상품등록 중 에러가 발생했습니다.");
            return "item/itemForm";

        }

        return "redirect:/";
    }

    //상품 수정페이지 보기
    @GetMapping(value = "/admin/item/{itemId}")
    public String itemDtl(@PathVariable("itemId") Long itemId, Model model) {
        try {
            ItemFormDto itemFormDto = itemService.getItemDtl(itemId);
            model.addAttribute("itemFormDto", itemFormDto);
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("errorMessage", "상품정보를 가져오는 도중 에러가 발생했습니다.");

            //에러발생시 비어있는 객체를 넘겨준다.
            model.addAttribute("itemFormDto", new ItemFormDto());
            return "item/itemModifyForm"; //등록화면으로 다시 이동
        }

        return "item/itemModifyForm";
    }

    //상품 수정(update)
    @PostMapping(value = "/admin/item/{itemId}")
    public String itemUpdate(@Valid ItemFormDto itemFormDto, Model model,
                             BindingResult bindingResult,
                             @RequestParam("itemImgFile") List<MultipartFile> itemImgFileList,
                             @PathVariable("itemId") Long itemId) {
        if (bindingResult.hasErrors()) return "item/itemForm"; // 유효성 체크에서 걸리면 다시 돌아간다.

        ItemFormDto getItemFormDto = itemService.getItemDtl(itemId);

        //상품등록 전에 첫번째 이미지가 있는지 없는지 검사(첫번째 이미지는 필수 입력값)
        // itemFormDto.getId() == null -> 이ㅏ미지 외에 다른 내용만 수정했을때 if문에 걸리는 경우 방지
        if (itemImgFileList.get(0).isEmpty() && itemFormDto.getId() == null) {
            model.addAttribute("errorMessage", "첫번째 상품 이미지는 필수 입력입니다");
            model.addAttribute("itemFormDto", getItemFormDto);
            return "item/itemModifyForm";
        }

        try {
            itemService.updateItem(itemFormDto, itemImgFileList);
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("errorMessage"
                    , "상품 수정 중 에러가 발생했습니다.");
            //에러 발생시 비어있는 객체를 넘겨준다.
            model.addAttribute("itemFormDto", getItemFormDto);
            return "item/itemModifyForm";

        }
        return "redirect:/";
    }

    //상품 관리 페이지
    @GetMapping(value = {"/admin/items", "/admin/items/{page}"})
    public String itemManage(ItemSearchDto itemSearchDto, @PathVariable("page") Optional<Integer> page,
                             Model model) {
        //PageRequst.of(페이지 번호, 한페이지당 조회할 데이터 개수)
        //URL path에 page가 있으면 해당 페이지 번호 조회, page가 없으면 0
        Pageable pageable = PageRequest.of(page.isPresent() ? page.get() : 0, 3);

        //검색어에 대한 정보, 페이징 처리하는 정보(페이지 번호, 한 페이지당 조회할 데이터 개수)
        Page<Item> items = itemService.getAdminImplPage(itemSearchDto,pageable);

        model.addAttribute("items", items);
        model.addAttribute("itemSearchDto", itemSearchDto);
        
        //상품관리 페이지 하단에 보여줄 최대 페이지 번호
        model.addAttribute("maxPage",5);


        return "item/itemMng";
    }

    //상품 전체 리스트
    @GetMapping(value = "/item/shop")
    public String itemShopList(Model model, ItemSearchDto itemSearchDto,
                               @RequestParam(value = "page") Optional<Integer> page) {
        Pageable pageable = PageRequest.of(page.isPresent() ? page.get() : 0, 6);
        Page<MainItemDto> items = itemService.getMainItemPage(itemSearchDto, pageable);

        model.addAttribute("items", items);
        model.addAttribute("itemSearchDto", itemSearchDto);
        model.addAttribute("maxPage", 5);


        return "item/itemShopList";
    }

    //상품 상세 페이지
    @GetMapping(value = "/item/{itemId}")
    public String itemDtl(Model model, @PathVariable(value = "itemId")Long itemId) {
        ItemFormDto itemFormDto = itemService.getItemDtl(itemId);
        model.addAttribute("item", itemFormDto);
        return "item/itemDtl";
    }

}
