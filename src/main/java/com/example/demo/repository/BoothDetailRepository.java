package com.example.demo.repository;

import java.util.HashMap;
import java.util.List;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.BoothDetail;

@Repository
public class BoothDetailRepository {

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public BoothDetailRepository(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    // ブース番号で場所を返す
    public String boothDetail(int boothNumber) {
        String sql = "SELECT booth_place FROM booth_detail WHERE booth_number = :booth_number";

        MapSqlParameterSource param = new MapSqlParameterSource()
                .addValue("booth_number", boothNumber);

        return namedParameterJdbcTemplate.queryForObject(sql, param, String.class);
    }

    // 全ブースリストを返す
    public List<BoothDetail> boothList() {
        String sql = "SELECT * FROM booth_detail";
        return namedParameterJdbcTemplate.query(
                sql,
                new MapSqlParameterSource(),
                new BeanPropertyRowMapper<>(BoothDetail.class));
    }

    // 施設番号（例: 5）からブース番号リストを取得
    public List<Integer> boothNumbers(int facilityNum) {
        String sql = "SELECT booth_number FROM booth_detail WHERE booth_place LIKE :facility";

        MapSqlParameterSource param = new MapSqlParameterSource()
                .addValue("facility", facilityNum + "%");

        return namedParameterJdbcTemplate.queryForList(sql, param, Integer.class);
    }

    // ブースがある施設一覧を取得
    public List<String> boothFacilityList() {
        String sql = "SELECT booth_place FROM booth_detail";

        return namedParameterJdbcTemplate.query(
                sql,
                new HashMap<>(),
                (rs, rowNum) -> rs.getString("booth_place")
        );
    }

    // 重複時にスキップしてブースを登録
    public void insertBoothPlace(int boothNumber, String boothPlace) {
        String sql = """
            INSERT INTO booth_detail (booth_number, booth_place)
            VALUES (:boothNumber, :boothPlace)
            ON CONFLICT (booth_number) DO NOTHING
        """;

        MapSqlParameterSource param = new MapSqlParameterSource()
                .addValue("boothNumber", boothNumber)
                .addValue("boothPlace", boothPlace);

        namedParameterJdbcTemplate.update(sql, param);
    }
}
