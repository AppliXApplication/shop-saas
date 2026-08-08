package com.applix.shop.cashier;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "`check`")
@Getter
@Setter
public class Check {

    @Id
    @Column(name = "id_check")
    private Integer id;

    @Column(name = "sum")
    private BigDecimal sum;

    @Column(name = "nal")
    private BigDecimal cash;

    @Column(name = "bnal")
    private BigDecimal cashless;

    @Column(name = "date")
    private LocalDateTime date;
}
