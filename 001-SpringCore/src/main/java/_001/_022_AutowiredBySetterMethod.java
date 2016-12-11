package _001;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Component;

//@Component //uncomment this first
public class _022_AutowiredBySetterMethod {
	public static void main(String[] args){
		ApplicationContext ctx = new ClassPathXmlApplicationContext("_013_BeanLifeCycle_Annotation.xml");
		System.out.println("Setter- "+ (_022_AutowiredBySetterMethod)ctx.getBean("_022_AutowiredBySetterMethod"));
	}

	private Address9 address99;
	
	//Autowiring on property need a default constructor, otherwise you get below error
	//Failed to instantiate: No default constructor found
	//If you dont specify any constructor, you always get default constructor. I am still writing one below.
	public _022_AutowiredBySetterMethod(){
	}
	
	//@Autowired fails by Type since there are 2 beans of Address9 type. Next it checks for qualifier, but there is none. So finally it autowires 
	//byName (spring bean with name address2). We don't care about the class property name.
	@Autowired
	public void setAddress(Address9 address2) {
		this.address99 = address2;
	}

	public String toString(){
		return address99.toString();
	}
}

/*
CONCLUSION:
1.) Constructor DI: looks at the constructor's params
2.) Setter DI: looks at the names of ALL setter methods
3.) Autowiring byName: looks at the names of ALL setter methods
4.) Autowiring byType: looks at the type of argument in the setter methods
5.) Autowiring byConstructor: looks at the constructor's arg list. And then first check byType and later byName
6.) Autowired Property: byType --> Qualifier --> byName of the property
7.) Autowired Constructor: byType --> Qualifier --> byName of constructor args list
8.) Autowired Setter: byType --> Qualifier --> byName  of setter args list
 */
