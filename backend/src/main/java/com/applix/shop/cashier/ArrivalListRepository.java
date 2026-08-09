package com.applix.shop.cashier;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ArrivalListRepository extends JpaRepository<ArrivalList, Long> {

    @Query("""
            select new com.applix.shop.cashier.GoodsMovementAggregate(
                al.goodsId, sum(al.amount), sum(al.amount * al.price)
            )
            from ArrivalList al
            where al.goodsId in :goodsIds
              and al.arrival.date >= :from and al.arrival.date < :to
            group by al.goodsId
            """)
    List<GoodsMovementAggregate> sumByGoodsAndDateRange(
            @Param("goodsIds") List<Long> goodsIds,
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to
    );
}
