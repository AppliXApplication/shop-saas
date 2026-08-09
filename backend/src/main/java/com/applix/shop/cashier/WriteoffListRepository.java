package com.applix.shop.cashier;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface WriteoffListRepository extends JpaRepository<WriteoffList, Long> {

    @Query("""
            select new com.applix.shop.cashier.GoodsMovementAggregate(
                wl.goodsId, sum(wl.amount), sum(wl.amount * wl.price)
            )
            from WriteoffList wl
            where wl.goodsId in :goodsIds
              and wl.writeoff.date >= :from and wl.writeoff.date < :to
            group by wl.goodsId
            """)
    List<GoodsMovementAggregate> sumByGoodsAndDateRange(
            @Param("goodsIds") List<Long> goodsIds,
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to
    );

    @Query("""
            select distinct wl.goodsId
            from WriteoffList wl
            where wl.writeoff.date >= :from and wl.writeoff.date < :to
              and (:categoryId is null or wl.goods.categoryId = :categoryId)
            """)
    List<Long> findDistinctGoodsIds(
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to,
            @Param("categoryId") Integer categoryId
    );

    @Query("""
            select coalesce(sum(wl.amount * wl.price), 0)
            from WriteoffList wl
            where wl.writeoff.date >= :from and wl.writeoff.date < :to
              and (:categoryId is null or wl.goods.categoryId = :categoryId)
            """)
    BigDecimal sumWriteoffValue(
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to,
            @Param("categoryId") Integer categoryId
    );
}
