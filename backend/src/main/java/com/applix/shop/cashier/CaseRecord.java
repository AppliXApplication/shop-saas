package com.applix.shop.cashier;

import com.applix.shop.users.User;
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

/**
 * В легаси-схеме у case_record нет реальных FK constraint'ов на связанные
 * таблицы (id_cashIn, id_cashOut, id_user, id_arrival, id_writeoff) — связи
 * заданы только на уровне JPA (insertable/updatable=false), как и для
 * Goods.category.
 */
@Entity
@Table(name = "case_record")
@Getter
@Setter
public class CaseRecord {

    @Id
    @Column(name = "id_case")
    private Long id;

    @Column(name = "date")
    private LocalDateTime date;

    @Column(name = "cashMustBe")
    private BigDecimal cashMustBe;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_cashIn", insertable = false, updatable = false)
    private CashIn cashIn;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_cashOut", insertable = false, updatable = false)
    private CashOut cashOut;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_user", insertable = false, updatable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_arrival", insertable = false, updatable = false)
    private Arrival arrival;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_writeoff", insertable = false, updatable = false)
    private Writeoff writeoff;
}
