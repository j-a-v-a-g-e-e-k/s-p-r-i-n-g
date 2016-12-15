package _001;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

public class _005_QueryForList {
	public static void main(String[] args) throws SQLException{
		ApplicationContext ctx = new ClassPathXmlApplicationContext("_002_JdbcTemplate.xml");
		UserDAO_005 userDAO = (UserDAO_005)ctx.getBean("userDAO_005");
		System.out.println(userDAO.getUser1());
		System.out.println(userDAO.getUser2());
		System.out.println(userDAO.getUser3());
		System.out.println(userDAO.getUser4());
		System.out.println(userDAO.getUser5());
		System.out.println(userDAO.getUser6());
		System.out.println(userDAO.getUser7());
		System.out.println(userDAO.getUser8());
	}
}

class UserRowMapper005 implements RowMapper<User_005>{
	public User_005 mapRow(ResultSet rs, int rowNum) throws SQLException {
		User_005 user = new User_005(rs.getInt(1),rs.getString(2),rs.getString(3));
		return user;
	}	
}

@Component
class UserDAO_005{	
	private JdbcTemplate jdbcTemplate;
	
	@Autowired
	public void setDataSource(DataSource dataSource){
		this.jdbcTemplate=new JdbcTemplate(dataSource);
	}
	
	public List<Map<String, Object>> getUser1(){
		List<Map<String, Object>> userName = jdbcTemplate.queryForList("SELECT * FROM USER_DETAILS");
		return userName;
	}
	
	public List<String> getUser2(){
		List<String> userName = jdbcTemplate.queryForList("SELECT USER_ID FROM USER_DETAILS", String.class);
		return userName;
	}
	
	public List<Map<String, Object>> getUser3(){
		List<Map<String, Object>> userName = jdbcTemplate.queryForList("SELECT USER_ID FROM USER_DETAILS WHERE ID=? and USER_ID=?", 9,"bharat");
		return userName;
	}
	
	public String getUser4(){
		String userName = jdbcTemplate.queryForObject("SELECT USER_ID FROM USER_DETAILS WHERE ID=?", new Object[]{9}, new int[]{Types.INTEGER},String.class);
		return userName;
	}
	
	public User_005 getUser5(){
		User_005 user = jdbcTemplate.queryForObject("SELECT * FROM USER_DETAILS WHERE ID=9", new UserRowMapper005());
		return user;
	}
	
	public User_005 getUser6(){
		User_005 user = jdbcTemplate.queryForObject("SELECT * FROM USER_DETAILS WHERE ID=?", new Object[]{9}, new UserRowMapper005());
		return user;
	}
	
	public User_005 getUser7(){
		User_005 user = jdbcTemplate.queryForObject("SELECT * FROM USER_DETAILS WHERE ID=? and USER_ID=?", new UserRowMapper005(), 9, "bharat");
		return user;
	}
	
	public User_005 getUser8(){
		User_005 user = jdbcTemplate.queryForObject("SELECT * FROM USER_DETAILS WHERE ID=?", new Object[]{9}, new int[]{Types.INTEGER}, new UserRowMapper005());
		return user;
	}	
}

class User_005{
	private int id;
	private String name;
	private String password;
	
	public User_005(int id, String name, String password) {
		this.id = id;
		this.name = name;
		this.password = password;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String toString(){
		return id + ": " + name + ": " + password;
	}
}

