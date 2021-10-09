package cn.jdfo.dao;

import cn.jdfo.tool.Pair;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Repository
public class MyDao {

    private final SessionFactory sessionFactory;

    @Autowired
    public MyDao(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    private Session getSession(){
        return sessionFactory.getCurrentSession();
    }

    public int save(Object object){
        Session session=getSession();
        return (int)session.save(object);
    }


    public <T> T get(Class<T> tClass, int id) {
        return getSession().get(tClass,id);
    }

    public <T> T get(Class<T> tClass, String key){
        Query<T> query=getSession().createQuery("from "+ tClass.getSimpleName()+" where key_='"+key+"'",tClass);
        return query.getSingleResult();
    }

    public <T> List<T> get(Class<T> tClass, List<String> names, List<Object> values){
        if(names==null||names.size()<1||values==null||values.size()<1||names.size()!=values.size())return null;
        Query<T> query=getSession().createQuery(buildQuery(names,tClass.getSimpleName()),tClass);
        for (int i=0,len=names.size();i<len;i++){
            query.setParameter(names.get(i),values.get(i));
        }
        return query.list();
    }

    public <T> List<T> get(Class<T> tClass, List<String> names, List<Object> values, String otherConditions, int limit, int offset){
        if(names==null||names.size()<1||values==null||values.size()<1||names.size()!=values.size())return null;
        Query<T> query=getSession().createQuery(buildQuery(names,tClass.getSimpleName())+otherConditions,tClass);
        for (int i=0,len=names.size();i<len;i++){
            query.setParameter(names.get(i),values.get(i));
        }
        query.setFirstResult(offset);
        query.setMaxResults(limit);
        return query.list();
    }

    public <T> T load(Class<T> tClass, int id){
        Session session=getSession();
        return session.load(tClass,id);
    }

    public void delete(Object object){
        Session session=getSession();
        session.delete(object);
    }

    public void delete(int id, Class tClass){
        Session session=getSession();
        Query query=session.createQuery("delete from "+tClass.getSimpleName()+" where id="+id);
        query.executeUpdate();
    }

    public void delete(List<String> names, List<Object> values, Class tClass){
        if(names==null||names.size()<1||values==null||values.size()<1||names.size()!=values.size())return;
        Session session=getSession();
        Query query=session.createQuery("delete "+buildQuery(names,tClass.getSimpleName()));
        for(int i=0,len=names.size();i<len;i++){
            query.setParameter(names.get(i),values.get(i));
        }
        query.executeUpdate();
    }

    public <T,K> void deleteOneToMany(int id_one, Class<T> oneClass, Class<K> manyClass){
        Session session=getSession();
        Query<K> query=session.createQuery("from "+manyClass.getSimpleName()+" where "+oneClass.getSimpleName()+"_id="+id_one, manyClass);
        if(query.list()!=null&&query.list().size()>0){
            for(K k: query.list()){
                session.delete(k);
            }
        }
    }

    public void update(Object object){
        Session session=getSession();
        session.saveOrUpdate(object);
    }

    public void update(List<Pair<String,Object>> conditions, List<Pair<String,Object>> values, Class aClass){
        Query query=getSession().createQuery("update "+aClass.getSimpleName()+buildUpdateValues(values)+buildUpdateConditions(conditions));
        query.executeUpdate();
    }

    public <T> List<T> allObjects(Class<T> tClass){
        Query<T> query=getSession().createQuery("from "+tClass.getSimpleName()+" order by id asc ",tClass);
        return query.list();
    }

    /**
     * 一对多查询
     * @param id_one 单个的ID
     * @param oneClass 单个的class
     * @param manyClass 多个的class
     */
    public <T> List<T> oneToMany(int id_one, Class oneClass, Class<T> manyClass){
        Query<T> query=getSession().createQuery("from "+manyClass.getSimpleName()+" where "+oneClass.getSimpleName()+"_id="+id_one,manyClass);
        return query.list();
    }

    public boolean hasObject(int id, Class tClass){
        Query<Long> query=getSession().createQuery("select count(*) from "+tClass.getSimpleName()+" where id="+id, Long.class);
        return query.getSingleResult()>0;
    }

    public boolean hasObject(String key, Class tClass){
        Query<Long> query=getSession().createQuery("select count(*) from "+tClass.getSimpleName()+" where key_='"+key+"'",Long.class);
        return query.getSingleResult()>0;
    }

    public boolean hasObject(List<String> columns, List<Object> values, Class tClass){
        if(columns==null||columns.isEmpty()||values==null||values.isEmpty()||columns.size()!=values.size())return false;
        Query<Long> query=getSession().createQuery("select count(*) "+buildQuery(columns,tClass.getSimpleName()),Long.class);
        for(int i=0,len=columns.size();i<len;i++){
            query.setParameter(columns.get(i),values.get(i));
        }
        return query.getSingleResult()>0;
    }
    public int getIdByKey(String key, Class aclass){
        Query<Integer> query=getSession().createQuery("select id from "+aclass.getSimpleName()+" where key_='"+key+"'", Integer.class);
        return query.getSingleResult();
    }

    public boolean hasRelation(int id1, int id2, Class class1, Class class2){
        Query query=getSession().createQuery("from "+class1.getSimpleName()+class2.getSimpleName()+" where "+class1.getSimpleName()+"_id="+id1+" and "+class2.getSimpleName()+"_id="+id2);
        return query.list()!=null&&query.list().size()>0;
    }

    public <T> T selectSingle(String query, Class<T> tClass){
        Query<T> query1=getSession().createQuery(query,tClass);
        return query1.getSingleResult();
    }

    private String buildQuery(List<String> columns, String tableName){
        StringBuilder builder=new StringBuilder("from "+tableName+" where 1=1");
        for(String column: columns){
            builder.append(" and ").append(column).append("= :").append(column);
        }
        return builder.toString();
    }
    private String buildUpdateValues(List<Pair<String,Object>> values){
        StringBuilder builder=new StringBuilder(" set ");
        for(int i=0;i<values.size();i++){
            Pair<String,Object> pair=values.get(i);
            builder.append(pair.getA()).append("=");
            if(pair.getB() instanceof String)builder.append("'").append(pair.getB()).append("'");
            else builder.append(pair.getB());
            if(i!=values.size()-1)builder.append(" , ");
        }
        return builder.toString();
    }
    private String buildUpdateConditions(List<Pair<String,Object>> conditions){
        StringBuilder builder=new StringBuilder(" where 1=1 ");
        for(Pair<String,Object> pair: conditions){
            builder.append(" and ").append(pair.getA()).append("=");
            if(pair.getB() instanceof String)builder.append("'").append(pair.getB()).append("'");
            else builder.append(pair.getB());
        }
        return builder.toString();
    }

    public static void main(String[] args) throws IOException {
        List<String> lines= Files.readAllLines(Paths.get("D:\\refseq.txt"));
        Set<String> stringSet=new HashSet<>();
        int dup=0;
        for (String s:lines.subList(1,lines.size())){
            String[] ss=s.split("\t");
            String name2=ss[1];
            if(stringSet.contains(name2)){
                System.out.println(name2);
                dup++;
            }
            else stringSet.add(name2);
        }
        System.out.println(dup);
    }

}
