package com.smashspot.stadium.model;

import java.util.*;
import java.sql.*;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

public class StadiumDAO implements StadiumDAO_interface {

	// 一個應用程式中,針對一個資料庫 ,共用一個DataSource即可
	private static DataSource ds = null;
	static {
		try {
			Context ctx = new InitialContext();
			ds = (DataSource) ctx.lookup("java:comp/env/jdbc/TestDB2");
		} catch (NamingException e) {
			e.printStackTrace();
		}
	}

	private static final String INSERT_STMT = 
			"INSERT INTO stadium (stdm_name, stdm_addr, loc_id, longitude, latitude, stdm_intro, court_count, court_price, opr_sta, stdm_pic, adm_id, business_hr) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
	private static final String GET_ALL_STMT = 
			"SELECT stdm_id, stdm_name, stdm_addr, loc_id, longitude, latitude, stdm_intro, court_count, court_price, opr_sta, stdm_pic, adm_id, business_hr, stdm_start_time FROM stadium ORDER BY stdm_id";
	private static final String GET_ONE_STMT = 
			"SELECT stdm_id, stdm_name, stdm_addr, loc_id, longitude, latitude, stdm_intro, court_count, court_price, opr_sta, stdm_pic, adm_id, business_hr, stdm_start_time FROM stadium WHERE stdm_id = ?";
	private static final String DELETE = 
			"DELETE FROM stadium WHERE stdm_id = ?";
	private static final String UPDATE = 
			"UPDATE stadium SET stdm_name=?, stdm_addr=?, loc_id=?, longitude=?, latitude=?, stdm_intro=?, court_count=?, court_price=?, opr_sta=?, stdm_pic=?, adm_id=?, business_hr=? WHERE stdm_id = ?";

	@Override
	public void insert(StadiumVO stadiumVO) {

		Connection con = null;
		PreparedStatement pstmt = null;

		try {

			con = ds.getConnection();
			pstmt = con.prepareStatement(INSERT_STMT);

			pstmt.setString(1, stadiumVO.getStdmName());       
			pstmt.setString(2, stadiumVO.getStdmAddr());      
			pstmt.setInt(3, stadiumVO.getLocId());             
			pstmt.setDouble(4, stadiumVO.getLongitude());     
			pstmt.setDouble(5, stadiumVO.getLatitude());       
			pstmt.setString(6, stadiumVO.getStdmIntro());      
			pstmt.setInt(7, stadiumVO.getCourtCount());        
			pstmt.setInt(8, stadiumVO.getCourtPrice());        
			pstmt.setBoolean(9, stadiumVO.getOprSta());        
			pstmt.setBytes(10, stadiumVO.getStdmPic());        
			pstmt.setInt(11, stadiumVO.getAdmId());            
			pstmt.setInt(12, stadiumVO.getBusinessHr());       
//			pstmt.setTimestamp(13, stadiumVO.getStdmStartTime());  
	
			pstmt.executeUpdate();

			// Handle any SQL errors
		} catch (SQLException se) {
			throw new RuntimeException("A database error occured. " + se.getMessage());
			// Clean up JDBC resources
		} finally {
			if (pstmt != null) {
				try {
					pstmt.close();
				} catch (SQLException se) {
					se.printStackTrace(System.err);
				}
			}
			if (con != null) {
				try {
					con.close();
				} catch (Exception e) {
					e.printStackTrace(System.err);
				}
			}
		}

	}

	@Override
	public void update(StadiumVO stadiumVO) {

		Connection con = null;
		PreparedStatement pstmt = null;

		try {

			con = ds.getConnection();
			pstmt = con.prepareStatement(UPDATE);

			pstmt.setInt(1, stadiumVO.getStdmId());
			pstmt.setString(2, stadiumVO.getStdmName());       
			pstmt.setString(3, stadiumVO.getStdmAddr());      
			pstmt.setInt(4, stadiumVO.getLocId());             
			pstmt.setDouble(5, stadiumVO.getLongitude());     
			pstmt.setDouble(6, stadiumVO.getLatitude());       
			pstmt.setString(7, stadiumVO.getStdmIntro());      
			pstmt.setInt(8, stadiumVO.getCourtCount());        
			pstmt.setInt(9, stadiumVO.getCourtPrice());        
			pstmt.setBoolean(10, stadiumVO.getOprSta());        
			pstmt.setBytes(11, stadiumVO.getStdmPic());        
			pstmt.setInt(12, stadiumVO.getAdmId());            
			pstmt.setInt(13, stadiumVO.getBusinessHr());       
//			pstmt.setTimestamp(13, stadiumVO.getStdmStartTime());  

			pstmt.executeUpdate();

			// Handle any driver errors
		} catch (SQLException se) {
			throw new RuntimeException("A database error occured. " + se.getMessage());
			// Clean up JDBC resources
		} finally {
			if (pstmt != null) {
				try {
					pstmt.close();
				} catch (SQLException se) {
					se.printStackTrace(System.err);
				}
			}
			if (con != null) {
				try {
					con.close();
				} catch (Exception e) {
					e.printStackTrace(System.err);
				}
			}
		}

	}

	@Override
	public void delete(Integer stdmId) {

		Connection con = null;
		PreparedStatement pstmt = null;

		try {

			con = ds.getConnection();
			pstmt = con.prepareStatement(DELETE);

			pstmt.setInt(1, stdmId);

			pstmt.executeUpdate();

			// Handle any driver errors
		} catch (SQLException se) {
			throw new RuntimeException("A database error occured. " + se.getMessage());
			// Clean up JDBC resources
		} finally {
			if (pstmt != null) {
				try {
					pstmt.close();
				} catch (SQLException se) {
					se.printStackTrace(System.err);
				}
			}
			if (con != null) {
				try {
					con.close();
				} catch (Exception e) {
					e.printStackTrace(System.err);
				}
			}
		}

	}

	@Override
	public StadiumVO findByPrimaryKey(Integer stdmId) {

		StadiumVO stadiumVO = null;
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {

			con = ds.getConnection();
			pstmt = con.prepareStatement(GET_ONE_STMT);

			pstmt.setInt(1, stdmId);

			rs = pstmt.executeQuery();

			while (rs.next()) {
				// stadiumVO 也稱為 Domain objects
				stadiumVO = new StadiumVO();
				stadiumVO.setStdmId(rs.getInt("stdm_id"));             
				stadiumVO.setStdmName(rs.getString("stdm_name"));      
				stadiumVO.setStdmAddr(rs.getString("stdm_addr"));      
				stadiumVO.setLocId(rs.getInt("loc_id"));              
				stadiumVO.setLongitude(rs.getDouble("longitude"));    
				stadiumVO.setLatitude(rs.getDouble("latitude"));     
				stadiumVO.setStdmIntro(rs.getString("stdm_intro"));    
				stadiumVO.setCourtCount(rs.getInt("court_count"));     
				stadiumVO.setCourtPrice(rs.getInt("court_price"));     
				stadiumVO.setOprSta(rs.getBoolean("opr_sta"));         
				stadiumVO.setStdmPic(rs.getBytes("stdm_pic"));        
				stadiumVO.setAdmId(rs.getInt("adm_id"));              
				stadiumVO.setBusinessHr(rs.getInt("business_hr"));     
				stadiumVO.setStdmStartTime(rs.getTimestamp("stdm_start_time"));  
			}

			// Handle any driver errors
		} catch (SQLException se) {
			throw new RuntimeException("A database error occured. " + se.getMessage());
			// Clean up JDBC resources
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException se) {
					se.printStackTrace(System.err);
				}
			}
			if (pstmt != null) {
				try {
					pstmt.close();
				} catch (SQLException se) {
					se.printStackTrace(System.err);
				}
			}
			if (con != null) {
				try {
					con.close();
				} catch (Exception e) {
					e.printStackTrace(System.err);
				}
			}
		}
		return stadiumVO;
	}

	@Override
	public List<StadiumVO> getAll() {
		List<StadiumVO> list = new ArrayList<StadiumVO>();
		StadiumVO stadiumVO = null;

		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {

			con = ds.getConnection();
			pstmt = con.prepareStatement(GET_ALL_STMT);
			rs = pstmt.executeQuery();

			while (rs.next()) {
				// stadiumVO 也稱為 Domain objects
				stadiumVO = new StadiumVO();
				stadiumVO.setStdmId(rs.getInt("stdm_id"));             
				stadiumVO.setStdmName(rs.getString("stdm_name"));      
				stadiumVO.setStdmAddr(rs.getString("stdm_addr"));      
				stadiumVO.setLocId(rs.getInt("loc_id"));              
				stadiumVO.setLongitude(rs.getDouble("longitude"));    
				stadiumVO.setLatitude(rs.getDouble("latitude"));     
				stadiumVO.setStdmIntro(rs.getString("stdm_intro"));    
				stadiumVO.setCourtCount(rs.getInt("court_count"));     
				stadiumVO.setCourtPrice(rs.getInt("court_price"));     
				stadiumVO.setOprSta(rs.getBoolean("opr_sta"));         
				stadiumVO.setStdmPic(rs.getBytes("stdm_pic"));        
				stadiumVO.setAdmId(rs.getInt("adm_id"));              
				stadiumVO.setBusinessHr(rs.getInt("business_hr"));     
				stadiumVO.setStdmStartTime(rs.getTimestamp("stdm_start_time"));  
				list.add(stadiumVO); // Store the row in the list
			}

			// Handle any driver errors
		} catch (SQLException se) {
			throw new RuntimeException("A database error occured. " + se.getMessage());
			// Clean up JDBC resources
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException se) {
					se.printStackTrace(System.err);
				}
			}
			if (pstmt != null) {
				try {
					pstmt.close();
				} catch (SQLException se) {
					se.printStackTrace(System.err);
				}
			}
			if (con != null) {
				try {
					con.close();
				} catch (Exception e) {
					e.printStackTrace(System.err);
				}
			}
		}
		return list;
	}
}