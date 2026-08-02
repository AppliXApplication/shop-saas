package com.applix.shop.goods;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "goods")
@Getter
@Setter
public class Goods {

    @Id
    @Column(name = "id_goods")
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "code", nullable = false)
    private String code;

    // Остаток на складе. В легаси-схеме нет FK на categorygoods,
    // связь задаём только на уровне JPA — в БД constraint отсутствует.
    @Column(name = "residue")
    private BigDecimal residue;

    @Column(name = "price")
    private BigDecimal price;

    @Column(name = "price_opt")
    private BigDecimal priceOpt;

    @Column(name = "date")
    private LocalDateTime date;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_categoryGoods", insertable = false, updatable = false)
    private CategoryGoods category;

    @Column(name = "id_categoryGoods", nullable = false)
    private Integer categoryId;

    @Column(name = "marking")
    private Boolean marking;
}
