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
@Table(name = "arrival")
@Getter
@Setter
public class Arrival {

    @Id
    @Column(name = "id_arrival")
    private Long id;

    @Column(name = "date")
    private LocalDateTime date;

    @Column(name = "sumInvoice")
    private BigDecimal sumInvoice;

    @Column(name = "sumArrival")
    private BigDecimal sumArrival;

    @Column(name = "number_waybill")
    private String numberWaybill;
}
