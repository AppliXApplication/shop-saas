package com.applix.shop.cashier;

import com.applix.shop.goods.Goods;
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

@Entity
@Table(name = "checklist")
@Getter
@Setter
public class CheckList {

    @Id
    @Column(name = "id_checkList")
    private Long id;

    @Column(name = "amount")
    private BigDecimal amount;

    @Column(name = "price")
    private BigDecimal price;

    @Column(name = "profit")
    private BigDecimal profit;

    @Column(name = "id_goods")
    private Long goodsId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_goods", insertable = false, updatable = false)
    private Goods goods;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_check", insertable = false, updatable = false)
    private Check check;
}

