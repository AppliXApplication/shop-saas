package com.applix.shop.goods;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "categorygoods")
@Getter
@Setter
public class CategoryGoods {

    @Id
    @Column(name = "id_categoryGoods")
    private Integer id;

    @Column(name = "name")
    private String name;

    @Column(name = "note")
    private String note;
}
