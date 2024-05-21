package movie;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class Movie {	
	private Scanner scanner = new Scanner(System.in);
	private Connection conn;
	
	public Movie() {
		try {			
			Class.forName("com.mysql.cj.jdbc.Driver");			
			
			conn = DriverManager.getConnection(
					"jdbc:mysql://localhost:3306/movie?useUnicode=true&characterEncoding=utf8",
					"root",
					"1234");
		}catch(Exception e) {
			e.printStackTrace();
			exit();
		}
	}

public void list() {
		try {
			String sql="SELECT no, title FROM movie order by no";
			PreparedStatement pstmt = conn.prepareStatement(sql); 
			ResultSet rs = pstmt.executeQuery(); 
			System.out.println("*** 영화 예매 사이트 입니다 ***");
			System.out.println("--------------현재 상영작--------------");
			while(rs.next()) {
				List List = new List();
				String no = rs.getString("no");
				String title = rs.getString("title");
				List.setNo(no);
				List.setTitle(title);				
				System.out.printf("%-7s", List.getTitle());}
			rs.close();
			pstmt.close();
		}catch(SQLException e) { 
			e.printStackTrace();
			exit();
		}
		System.out.println();
		System.out.println("------------------------------------");
		System.out.println("1.로그인 | 2.회원가입 | 3.프로그램 종료");
		System.out.println("선택 :");
		String menuNo = scanner.nextLine();
		System.out.println();
		
		switch(menuNo) {
					case "1" -> login();
					case "2" -> create();
					case "3" -> exit();
					default -> list();
}
		
}

public void login() {
	
	try {
        System.out.println("로그인");
        System.out.print("아이디 :");
        String inputID = scanner.nextLine();
        System.out.print("비밀번호 :");
        String inputPW = scanner.nextLine();
        
        String adminID = "추승보";
		String adminPW = "qwer";
		
        String sql = "SELECT * FROM member WHERE id=?";
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setString(1, inputID);
        ResultSet rs = pstmt.executeQuery();

        if (inputID.equals(adminID) && inputPW.equals(adminPW)) {
			System.out.println();
			System.out.println("관리자 아이디입니다.");
			admin();}
		else if (rs.next()) {
            String pwd = rs.getString("pwd");
            if (inputPW.equals(pwd)) {
                System.out.println();
                System.out.println("로그인 되었습니다.");
                System.out.println();
                pstmt.close();
                user(inputID);
            } else {
                System.out.println("비밀번호가 틀렸습니다.");
                System.out.println();
                login();
            }
        } else {
            System.out.println("해당 아이디를 찾을 수 없습니다.");
            System.out.println();
            list();
        }
    } catch (SQLException e) {
        list();
    }
}

public void user(String memberId)	{
	System.out.println("1.예매하기 | 2.회원수정 | 3.로그아웃");
	System.out.println("선택 :");
	String menuNo = scanner.nextLine();
	System.out.println();
	
	switch(menuNo) {
				case "1" -> reservation(memberId);
				case "2" -> read(memberId);
				case "3" -> list();
				default -> list();
				}
}
	
public void create() {
	Member member = new Member();
	System.out.println("[회원가입]"); 
	System.out.print("아이디 :");
	member.setId(scanner.nextLine()); 
	System.out.print("비밀번호 :");
	member.setPwd(scanner.nextLine());
	System.out.print("이름 :");
	member.setName(scanner.nextLine());
	System.out.print("email :");
	member.setEmail(scanner.nextLine());

	System.out.println("------------------------------------");
	System.out.println("가입하시겠습니까?");
	System.out.println("1.가입하기 | 2.취소하기");
	System.out.println("선택 :");
	String menuNo = scanner.nextLine();
	if(menuNo.equals("1")) {					
		try {
			String sql=""+ "INSERT INTO member (id, pwd, name, email, joinDate)" +
						"VALUES(?, ?, ?, ?, now())";
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, member.getId());
			pstmt.setString(2, member.getPwd());
			pstmt.setString(3, member.getName());
			pstmt.setString(4, member.getEmail());
			pstmt.executeUpdate();
			System.out.println("가입이 완료되었습니다.");
			System.out.println();
			pstmt.close();
		}catch(Exception e) {			
			System.out.println("중복된 아이디 입니다.");
			create();
		}
		
}

	list();
}

public void read(String memberId) {	
	try {
		String sql = "SELECT id, name, email, joinDate FROM member WHERE id=?";
		PreparedStatement pstmt = conn.prepareStatement(sql);
		pstmt.setString(1, memberId);
		ResultSet rs = pstmt.executeQuery();
		
		if(rs.next()) {
			Member member = new Member();
			String id= rs.getString("id");
			String name = rs.getString("name"); 
			String email = rs.getString("email");
			Date joinDate = rs.getDate("joinDate");
			member.setId(id);
			member.setName(name);
			member.setEmail(email);
			member.setJoinDate(joinDate);
			System.out.println("[회원정보]");
			System.out.println("아이디 :" + member.getId());
			System.out.println("이름 :" + member.getName());
			System.out.println("email :" + member.getEmail());
			System.out.println("가입날짜 :" + member.getJoinDate());
		
			System.out.println("------------------------------------");
			System.out.println("1.수정하기 | 2.탈퇴하기");
			System.out.print("선택 :");
			String menuNo = scanner.nextLine(); 
			System.out.println();
			
			if(menuNo.equals("1")) { update(member);
			} else if(menuNo.equals("2")) {
				delete(member); 
				}
		}
		rs.close();
		pstmt.close();
		} catch (Exception e) {
			e.printStackTrace(); 
			exit(); 
			}
	read(memberId);
	}

public void update(Member member) {
	System.out.println("[회원수정]");
	System.out.print("이름 :");
	member.setName(scanner.nextLine());
	System.out.print("비밀번호 :");
	member.setPwd(scanner.nextLine());
	System.out.print("email :");
	member.setEmail(scanner.nextLine());
	
	System.out.println("수정을 완료하시겠습니까?");
	System.out.println("1.수정하기 | 2.취소하기");
	System.out.print("선택 :");
	String menuNo = scanner.nextLine();
	if(menuNo.equals("1")) {
			try { String sql = "" +
								"UPDATE member SET pwd=?, name=?, email=? WHERE id=?"; 
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, member.getPwd());
			pstmt.setString(2, member.getName()); 
			pstmt.setString(3, member.getEmail()); 
			pstmt.setString(4, member.getId());
			System.out.println("수정이 완료되엇습니다.");
			System.out.println();
			pstmt.executeUpdate();
			pstmt.close();
			} catch (Exception e) {
				e.printStackTrace(); 
				exit();
				}
			}			
	user(member.getId());
}

public void delete(Member member) {
    try {
        String sql = "DELETE FROM member WHERE id=?";
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setString(1, member.getId());
        System.out.println("정말로 탈퇴하시겠습니까?");
        System.out.println("1.탈퇴하기 | 2.취소하기");
        System.out.println("선택 :");
        String menuNo = scanner.nextLine();
		if(menuNo.equals("1")) {delete(member);		
		}
		System.out.println("탈퇴가 완료되었습니다");
		System.out.println();
        pstmt.close();
    } catch (SQLException e) {  
        e.printStackTrace();
        exit();
    }
    
    list();
}

public void reservation(String memberId) {
		System.out.println("--------------현재 상영작--------------");
    try {
        String sqlList = "SELECT no, title FROM movie ORDER BY no";
        PreparedStatement pstmtList = conn.prepareStatement(sqlList);
        ResultSet rsList = pstmtList.executeQuery();
        while (rsList.next()) { 
            String title = rsList.getString("title");
            System.out.printf("%-7s", title);           
        }
        
        rsList.close();
        pstmtList.close();
        
        System.out.println();
        System.out.println("------------------------------------");
        System.out.println("예매할 영화제목을 입력해주세요.");
        System.out.print("영화제목 :");
        String movieTitle = scanner.nextLine();


        String sqlInsert = "INSERT INTO reservation (id, title) VALUES(?, ?)";
        PreparedStatement pstmtInsert = conn.prepareStatement(sqlInsert);
        pstmtInsert.setString(1, memberId);
        pstmtInsert.setString(2, movieTitle);
        pstmtInsert.executeUpdate();
        pstmtInsert.close();

        System.out.println("예매가 완료되었습니다.");
        System.out.println();        
    } catch (Exception e) {
        e.printStackTrace();
        exit();
    }
    user(memberId);
}

public void admin() {
	while (true) {
		System.out.println("1.예매현황 | 2.영화관리 | 3.로그아웃");		
		System.out.print("선택 :");
		int adminmenu = scanner.nextInt();
		scanner.nextLine();
		
		switch (adminmenu) {
		case 1:
			try { String sql = "SELECT id, title FROM reservation";
					PreparedStatement pstmt = conn.prepareStatement(sql);
					pstmt.executeQuery();
					ResultSet rs = pstmt.executeQuery();
					while (rs.next()) {        	
				      String id = rs.getString("id");
				      String title = rs.getString("title");
				      System.out.printf("%-5s%-7s \n", id, title);				      
				    }
					System.out.println();
					System.out.println("1.예매취소 | 2.회원삭제 | 3.돌아가기");
					System.out.print("선택 :");
					String No = scanner.nextLine();
					switch(No) {
						case "1" -> cancel();
						case "2" -> clean();
						case "3" -> admin();
					}
					
					rs.close();
					pstmt.close(); 
			} catch (Exception e) {
				e.printStackTrace(); 
				admin();		
			}
			break;
		case 2:
			try {
				String sql="SELECT no, title FROM movie order by no";
				PreparedStatement pstmt = conn.prepareStatement(sql); 
				ResultSet rs = pstmt.executeQuery(); 
				System.out.println("** 영화 예매 사이트 입니다 **");
				System.out.println("--------------현재 상영작--------------");
				
				while(rs.next()) {
					List List = new List();
					String no = rs.getString("no");
					String title = rs.getString("title");
					List.setNo(no);
					List.setTitle(title);				
					System.out.printf("%-7s", List.getTitle());}					
						
						System.out.println();
						System.out.println("--------------------------------------");
						System.out.println("1.영화등록 | 2.영화삭제 | 3.돌아가기");		
						System.out.print("선택 :");
						String No = scanner.nextLine();
						switch(No) {
							case "1" -> createmovie();
							case "2" -> deletemovie();
							case "3" -> admin();
						}
					
				rs.close();
				pstmt.close();
				}catch(SQLException e) { 
				e.printStackTrace();
				exit();					
			}			
				break;		
		case 3:
			System.out.println("로그아웃되었습니다.");
			System.out.println();			
			break;
		default:
			System.out.println("올바른 선택지를 입력하세요.");
			System.out.println();
			break;
		 }
		list();
	}
}

public void createmovie() {
		List list = new List();
		
		System.out.print("영화제목 :");
		list.setTitle(scanner.nextLine()); 
		
	try {
		String sql=""+ "INSERT INTO movie (title)" +
					"VALUES(?)";
		PreparedStatement pstmt = conn.prepareStatement(sql);
		pstmt.setString(1, list.getTitle());
		pstmt.executeUpdate();
		System.out.println("영화 등록이 완료되었습니다.");
		pstmt.close();
	}catch(Exception e) {
		System.out.println("영화 등록을 실패하였습니다.");
		exit();
	}
	admin();
}

public void deletemovie() {
	List list = new List();
	
	System.out.print("영화제목 :");
	list.setTitle(scanner.nextLine()); 
	
	try {
		String sql=""+ "DELETE FROM movie WHERE title=?";
		PreparedStatement pstmt = conn.prepareStatement(sql);
		pstmt.setString(1, list.getTitle());
		pstmt.executeUpdate();
		System.out.println("영화 삭제가 완료되었습니다.");
		pstmt.close();
	}catch(Exception e) {
		System.out.println("영화 삭제를 실패하였습니다.");
		exit();
	}
admin();
}

public void cancel() {
	 System.out.println("예매를 취소시킬 아이디와 영화제목을 입력해주세요");	
	 System.out.println("아이디 :"); 
	 String reservationID = scanner.nextLine();
	 System.out.println("영화제목 :");
	 String reservationTitle = scanner.nextLine();
	 
	 try {
		 String sql = "DELETE FROM reservation WHERE id=? and title=?";
		 PreparedStatement pstmt = conn.prepareStatement(sql);
		 pstmt.setString(1, reservationID);
		 pstmt.setString(2, reservationTitle);
		 pstmt.executeUpdate();
		 System.out.println("예매를 취소시켰습니다.");
		 System.out.println();
		 pstmt.close(); 		 					
		 } catch (Exception e) {
			 System.out.println("취소를 실패하였습니다.");
			 cancel();		 						
			 }
	 admin();
	 }

public void clean() {
	 System.out.println("삭제할 아이디를 입력해주세요");	
	 System.out.print("회원아이디 :");
	 String memberID = scanner.nextLine();
	 
		 				try {
		 					String sql = "DELETE FROM member WHERE id=?";
		 					PreparedStatement pstmt = conn.prepareStatement(sql);
		 					pstmt.setString(1, memberID);
		 					pstmt.executeUpdate();
		 					System.out.println("회원을 삭제하였습니다.");
		 					System.out.println();
		 					pstmt.close(); 
		 					
		 					} catch (Exception e) {
		 						System.out.println("삭제를 실패하였습니다.");
		 						clean();		 						
		 					}
		 				admin();
		 				}

public void exit() {
			if(conn !=null) {
						try {
								conn.close();
						}catch (SQLException e) {
						}
			}
			System.out.println("*** 예매 사이트 종료 ***");
			System.exit(0);
}



public static void main(String[] args) {
	Movie movie= new Movie();
	movie.list();
	}		
}