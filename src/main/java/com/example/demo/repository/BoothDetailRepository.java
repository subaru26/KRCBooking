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
	
	//ブース番号で場所だったりをかえす
	public String boothDetail(int boothNumber) {
		
		String sql = "SELECT booth_place FROM booth_detail WHERE booth_number = :booth_number";
		
		MapSqlParameterSource param = new MapSqlParameterSource()
				.addValue("booth_number", boothNumber);
		
		String boothDetail = namedParameterJdbcTemplate.queryForObject(sql, param, String.class);
		
		return boothDetail;
	}
	
	// ブースの数を返す（行数をカウント）
	public List<BoothDetail> boothList() {
	    String sql = "SELECT * FROM booth_detail;";
	    return namedParameterJdbcTemplate.query(sql, new MapSqlParameterSource(), new BeanPropertyRowMapper<>(BoothDetail.class));
	}
	//ブースの施設番号を元にブース番号返す
	public List<Integer> boothNumbers(int facilityNum){
		
		String sql = "SELECT booth_number FROM booth_detail WHERE booth_place LIKE :facility;";
		
		MapSqlParameterSource param = new MapSqlParameterSource()
				.addValue("facility", facilityNum + "%");
		
		return namedParameterJdbcTemplate.queryForList(sql, param, Integer.class);		
	}
	
	//ブースがある施設を返す
	public List<String> boothFacilityList(){
		
		String sql = "SELECT booth_place FROM booth_detail";
		
		return namedParameterJdbcTemplate.query(
				sql,
				new HashMap<>(),
				(rs,rowNum) -> rs.getString("booth_place")
			);
	}
public void insertBoothPlace(String boothPlace) {
		
		String sql = "insert into booth_detail (booth_place) values (:boothPlace)";
		
		MapSqlParameterSource param = new MapSqlParameterSource()
				.addValue("boothPlace", boothPlace);
		
		namedParameterJdbcTemplate.update(sql, param);
		
	}
}