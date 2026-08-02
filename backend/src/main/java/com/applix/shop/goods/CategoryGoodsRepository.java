package com.applix.shop.goods;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CategoryGoodsRepository extends JpaRepository<CategoryGoods, Integer> {

    @Query("select new com.applix.shop.goods.CategoryDTO(c.id, c.name) from CategoryGoods c order by c.name asc")
    List<CategoryDTO> findAllForFilter();
}
