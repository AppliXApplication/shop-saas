package com.applix.shop.cashier;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "cash_out")
@Getter
@Setter
public class CashOut {

    @Id
    @Column(name = "id_cashOut")
    private Integer id;

    @Column(name = "sum_cash")
    private BigDecimal sumCash;

    @Column(name = "note")
    private String note;
}
