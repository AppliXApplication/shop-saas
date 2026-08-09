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
@Table(name = "total")
@Getter
@Setter
public class Total {

    @Id
    @Column(name = "id_total")
    private Integer id;

    @Column(name = "date")
    private LocalDateTime date;

    /** "Излишек" — расхождение между фактической и ожидаемой суммой в кассе. */
    @Column(name = "spare")
    private BigDecimal spare;

    @Column(name = "sumGoods")
    private BigDecimal sumGoods;

    @Column(name = "proceeds")
    private BigDecimal proceeds;

    @Column(name = "nal")
    private BigDecimal cash;

    @Column(name = "bnal")
    private BigDecimal cashless;
}
