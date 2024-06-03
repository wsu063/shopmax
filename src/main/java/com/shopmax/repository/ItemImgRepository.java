package com.shopmax.repository;

import com.shopmax.entity.ItemImg;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ItemImgRepository extends JpaRepository<ItemImg, Long> {
    // select * from item_img where item_id = ? order by asc;
    List<ItemImg> findByItemIdOrderByIdAsc(Long ItemId);

    //select * from item_img where item_id = ? and rep_img_yn = ?
    ItemImg findByItemIdAndRepImgYn(Long itemId, String repImgYn);

}
