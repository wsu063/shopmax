package com.shopmax.dto;

import com.shopmax.entity.ItemImg;
import lombok.Getter;
import lombok.Setter;
import org.modelmapper.ModelMapper;

@Getter
@Setter
public class ItemImgDto {
    private Long id;

    private String imgName;

    private String oriImgName;

    private String imgUrl;

    private String repImgYN;

    private static ModelMapper modelMapper = new ModelMapper();

    // entity -> dto
    public static ItemImgDto of(ItemImg itemImg) {
        return modelMapper.map(itemImg, ItemImgDto.class); // ItemImgDto 객체를 리턴
    }
}
