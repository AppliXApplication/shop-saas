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
@Table(name = "writeoff")
@Getter
@Setter
public class Writeoff {

    @Id
    @Column(name = "id_writeoff")
    private Long id;

    @Column(name = "date")
    private LocalDateTime date;

    @Column(name = "sum")
    private BigDecimal sum;

    @Column(name = "note")
    private String note;
}
