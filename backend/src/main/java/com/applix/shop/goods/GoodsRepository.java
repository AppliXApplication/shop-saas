package com.applix.shop.goods;

import com.applix.shop.report.GoodsResidueRow;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;

public interface GoodsRepository extends JpaRepository<Goods, Long> {

    @Query("""
            select new com.applix.shop.report.GoodsResidueRow(
                g.id, g.name, g.code, c.name, g.residue, g.price
            )
            from Goods g
            left join g.category c
            where (:search is null or lower(g.name) like lower(concat('%', :search, '%')))
              and (:categoryId is null or g.categoryId = :categoryId)
            order by g.name asc
            """)
    Page<GoodsResidueRow> findResidueReport(
            @Param("search") String search,
            @Param("categoryId") Integer categoryId,
            Pageable pageable
    );

    @Query("""
            select new com.applix.shop.report.GoodsResidueRow(
                g.id, g.name, g.code, c.name, g.residue, g.price
            )
            from Goods g
            left join g.category c
            where g.id in :ids
              and (:categoryId is null or g.categoryId = :categoryId)
            order by g.name asc
            """)
    Page<GoodsResidueRow> findResidueReportByIds(
            @Param("ids") java.util.List<Long> ids,
            @Param("categoryId") Integer categoryId,
            Pageable pageable
    );

    /**
     * Сумма остатков (кол-во * цена) с учётом фильтра по категории.
     * Считается агрегатом в БД, а не суммированием на фронте по одной странице —
     * иначе сумма была бы только по видимым 50 строкам, а не по всей выборке.
     */
    @Query("""
            select coalesce(sum(g.residue * g.price), 0)
            from Goods g
            where (:categoryId is null or g.categoryId = :categoryId)
            """)
    BigDecimal sumResidueValue(@Param("categoryId") Integer categoryId);
}


