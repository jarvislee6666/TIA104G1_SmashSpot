package com;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

import javax.persistence.ManyToOne;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.smashspot.stadium.model.StadiumVO;

import util.HibernateUtil_CompositeQuery_Stdm;

import com.smashspot.stadium.model.StdmRepository;

@SpringBootApplication
public class Test_Application_CommandLineRunner_Stdm implements CommandLineRunner {
    
	@Autowired
	StdmRepository repository ;
	
	@Autowired
    private SessionFactory sessionFactory;
	
	public static void main(String[] args) {
        SpringApplication.run(Test_Application_CommandLineRunner_Stdm.class);
    }

    @Override
    public void run(String...args) throws Exception {

//    	//● 新增
//		DeptVO deptVO = new DeptVO(); // 部門POJO
//		deptVO.setDeptno(1);

//		EmpVO empVO1 = new EmpVO();
//		empVO1.setEname("吳永志1");
//		empVO1.setJob("MANAGER");
//		empVO1.setHiredate(java.sql.Date.valueOf("2005-01-01"));
//		empVO1.setSal(new Double(50000));
//		empVO1.setComm(new Double(500));
//		empVO1.setDeptVO(deptVO);
//		repository.save(empVO1);

		//● 修改
//		EmpVO empVO2 = new EmpVO();
//		empVO2.setEmpno(7001);
//		empVO2.setEname("吳永志2");
//		empVO2.setJob("MANAGER2");
//		empVO2.setHiredate(java.sql.Date.valueOf("2002-01-01"));
//		empVO2.setSal(new Double(20000));
//		empVO2.setComm(new Double(200));
//		empVO2.setDeptVO(deptVO);
//		repository.save(empVO2);
		
		//● 刪除   //●●● --> EmployeeRepository.java 內自訂的刪除方法
//		repository.deleteByEmpno(7014);
		
		//● 刪除   //XXX --> Repository內建的刪除方法目前無法使用，因為有@ManyToOne
		//System.out.println("--------------------------------");
		//repository.deleteById(7001);      
		//System.out.println("--------------------------------");

    	//● 查詢-findByPrimaryKey (多方emp2.hbm.xml必須設為lazy="false")(優!)
//    	Optional<EmpVO> optional = repository.findById(7001);
//		EmpVO empVO3 = optional.get();
//		System.out.print(empVO3.getEmpno() + ",");
//		System.out.print(empVO3.getEname() + ",");
//		System.out.print(empVO3.getJob() + ",");
//		System.out.print(empVO3.getHiredate() + ",");
//		System.out.print(empVO3.getSal() + ",");
//		System.out.print(empVO3.getComm() + ",");
//		// 注意以下三行的寫法 (優!)
//		System.out.print(empVO3.getDeptVO().getDeptno() + ",");
//		System.out.print(empVO3.getDeptVO().getDname() + ",");
//		System.out.print(empVO3.getDeptVO().getLoc());
//		System.out.println("\n---------------------");
      
    	
		//● 查詢-getAll (多方emp2.hbm.xml必須設為lazy="false")(優!)

//     	List<StadiumVO> list = repository.findAll();
// 		for (StadiumVO aStdm : list) {
// 			System.out.print(aStdm.getStdmId() + ",");
// 			System.out.print(aStdm.getStdmName() + ",");
// 			System.out.print(aStdm.getStdmAddr() + ",");
// 			//System.out.print(aStdm.getLocId() + ",");
// 			System.out.print(aStdm.getLongitude() + ",");
// 			System.out.print(aStdm.getLatitude() + ",");
// 			System.out.print(aStdm.getStdmIntro() + ",");
// 			System.out.print(aStdm.getCourtCount() + ",");
// 			System.out.print(aStdm.getCourtPrice() + ",");
// 			System.out.print(aStdm.getOprSta() + ",");
// 			System.out.print(aStdm.getStdmPic() + ",");
// 			//System.out.print(aStdm.getAdmin() + ",");
// 			System.out.print(aStdm.getOpenTime() + ",");
// 			System.out.print(aStdm.getCloseTime() + ",");
// 			System.out.print(aStdm.getStdmStartTime() + ",");
// 			// 注意以下三行的寫法 (優!)
// 			System.out.print(aStdm.getLocationVO().getLocId() + ",");
// 			System.out.print(aStdm.getAdmVO().getAdmid() + ",");
// 			System.out.println();
// 		}



//		//● 複合查詢-getAll(map) 配合 req.getParameterMap()方法 回傳 java.util.Map<java.lang.String,java.lang.String[]> 之測試
//		Map<String, String[]> map = new TreeMap<String, String[]>();
//		map.put("empno", new String[] { "7001" });
//		map.put("ename", new String[] { "KING" });
//		map.put("job", new String[] { "PRESIDENT" });
//		map.put("hiredate", new String[] { "1981-11-17" });
//		map.put("sal", new String[] { "5000.5" });
//		map.put("comm", new String[] { "0.0" });
//		map.put("deptno", new String[] { "1" });

//		
//		//● 刪除   //●●● --> EmployeeRepository.java 內自訂的刪除方法
////		repository.deleteByEmpno(7014);
//		
//		//● 刪除   //XXX --> Repository內建的刪除方法目前無法使用，因為有@ManyToOne
//		//System.out.println("--------------------------------");
//		//repository.deleteById(7001);      
//		//System.out.println("--------------------------------");
//
//    	//● 查詢-findByPrimaryKey (多方emp2.hbm.xml必須設為lazy="false")(優!)
////    	Optional<EmpVO> optional = repository.findById(7001);
////		EmpVO empVO3 = optional.get();
////		System.out.print(empVO3.getEmpno() + ",");
////		System.out.print(empVO3.getEname() + ",");
////		System.out.print(empVO3.getJob() + ",");
////		System.out.print(empVO3.getHiredate() + ",");
////		System.out.print(empVO3.getSal() + ",");
////		System.out.print(empVO3.getComm() + ",");
////		// 注意以下三行的寫法 (優!)
////		System.out.print(empVO3.getDeptVO().getDeptno() + ",");
////		System.out.print(empVO3.getDeptVO().getDname() + ",");
////		System.out.print(empVO3.getDeptVO().getLoc());
////		System.out.println("\n---------------------");
//      
//    	
//		//● 查詢-getAll (多方emp2.hbm.xml必須設為lazy="false")(優!)
//    	List<StadiumVO> list = repository.findAll();
//		for (StadiumVO aStdm : list) {
//			System.out.print(aStdm.getStdmId() + ",");
//			System.out.print(aStdm.getStdmName() + ",");
//			System.out.print(aStdm.getStdmAddr() + ",");
//			//System.out.print(aStdm.getLocId() + ",");
//			System.out.print(aStdm.getLongitude() + ",");
//			System.out.print(aStdm.getLatitude() + ",");
//			System.out.print(aStdm.getStdmIntro() + ",");
//			System.out.print(aStdm.getCourtCount() + ",");
//			System.out.print(aStdm.getCourtPrice() + ",");
//			System.out.print(aStdm.getOprSta() + ",");
//			System.out.print(aStdm.getStdmPic() + ",");
//			//System.out.print(aStdm.getAdmin() + ",");
//			System.out.print(aStdm.getOpenTime() + ",");
//			System.out.print(aStdm.getCloseTime() + ",");
//			System.out.print(aStdm.getStdmStartTime() + ",");
//			System.out.print(aStdm.getStdmPicBase64() + ",");
//			// 注意以下三行的寫法 (優!)
////			System.out.print(aStdm.getLocationVO().getLocId() + ",");
////			System.out.print(aStdm.getAdmVO().getAdmid() + ",");
//			System.out.println();
//		}
//
//
////		//● 複合查詢-getAll(map) 配合 req.getParameterMap()方法 回傳 java.util.Map<java.lang.String,java.lang.String[]> 之測試
////		Map<String, String[]> map = new TreeMap<String, String[]>();
////		map.put("empno", new String[] { "7001" });
////		map.put("ename", new String[] { "KING" });
////		map.put("job", new String[] { "PRESIDENT" });
////		map.put("hiredate", new String[] { "1981-11-17" });
////		map.put("sal", new String[] { "5000.5" });
////		map.put("comm", new String[] { "0.0" });
////		map.put("deptno", new String[] { "1" });
////		
////		List<EmpVO> list2 = HibernateUtil_CompositeQuery_Emp3.getAllC(map,sessionFactory.openSession());
////		for (EmpVO aEmp : list2) {
////			System.out.print(aEmp.getEmpno() + ",");
////			System.out.print(aEmp.getEname() + ",");
////			System.out.print(aEmp.getJob() + ",");
////			System.out.print(aEmp.getHiredate() + ",");
////			System.out.print(aEmp.getSal() + ",");
////			System.out.print(aEmp.getComm() + ",");
////			// 注意以下三行的寫法 (優!)
////			System.out.print(aEmp.getDeptVO().getDeptno() + ",");
////			System.out.print(aEmp.getDeptVO().getDname() + ",");
////			System.out.print(aEmp.getDeptVO().getLoc());
////			System.out.println();
////		}
//		
//
//		//● (自訂)條件查詢
////		List<EmpVO> list3 = repository.findByOthers(7001,"%K%",java.sql.Date.valueOf("1981-11-17"));
////		if(!list3.isEmpty()) {
////			for (EmpVO aEmp : list3) {
////				System.out.print(aEmp.getEmpno() + ",");
////				System.out.print(aEmp.getEname() + ",");
////				System.out.print(aEmp.getJob() + ",");
////				System.out.print(aEmp.getHiredate() + ",");
////				System.out.print(aEmp.getSal() + ",");
////				System.out.print(aEmp.getComm() + ",");
////				// 注意以下三行的寫法 (優!)
////				System.out.print(aEmp.getDeptVO().getDeptno() + ",");
////				System.out.print(aEmp.getDeptVO().getDname() + ",");
////				System.out.print(aEmp.getDeptVO().getLoc());
////				System.out.println();
////			}
////		} else System.out.print("查無資料\n");
////		System.out.println("--------------------------------");
//
//    }
//}
//
//
