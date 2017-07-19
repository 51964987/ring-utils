package ringutils.hibernate;

import java.util.Collections;

import org.hibernate.SessionFactory;
import org.hibernate.hql.internal.ast.QueryTranslatorImpl;
import org.hibernate.internal.SessionFactoryImpl;

public class HibernateUtil {
	
	/** 
     * 将hql语句转换为sql语句,无参数 
     * @param sessionFactory session工厂
     * @param hql 要转换的hql语句 
     * @return 可执行的sql语句, 当返回null, 可以通过getResultMsg()方法查看处理结果信息 
     */  
    public static String transHqlToSql(SessionFactory sessionFactory,String hql) {  
        // 当hql为null或空时,直接返回null  
        if (hql == null || hql.equals("")) {  
            return "";  
        }  
        // 获取当前session  
        // 得到session工厂实现类  
        SessionFactoryImpl sfi = (SessionFactoryImpl) sessionFactory;  
        // 得到Query转换器实现类  
        QueryTranslatorImpl queryTranslator = new QueryTranslatorImpl(hql, hql, Collections.EMPTY_MAP, sfi);  
        queryTranslator.compile(Collections.EMPTY_MAP, false);  
        // 得到sql  
        String sql = queryTranslator.getSQLString();  
        return sql;  
    }
    
}
