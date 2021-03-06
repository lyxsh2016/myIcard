package zhuaizhuai.icard;

import android.util.Log;
import android.widget.Toast;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Created by lyxsh on 2016/7/20.
 */
public class jTDS
{
    Map<String,Integer> index = new HashMap<String, Integer>();

    public String Ipaddress = "zhuaizhuai.pub";
    public Connection con = null;
    public float oldbalance = 0;

    public float getOldbalance()
    {
        return oldbalance;
    }

    final int J_id = 1,J_time = 2,J_io = 3,J_detail = 4,J_oldbalance = 5,J_yonghuming = 6,J_leixing = 7,J_beizhu = 8;
    final int D_id = 1,D_neixing = 2,D_yonghuming = 3,D_biaoti = 4,D_shijian = 5,D_neirong = 6;
    final int H_id = 1,H_zhutiid = 2,H_yonghuming = 3,H_neirong = 4,H_shijian = 5;

    SimpleDateFormat formattime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public void lianjie()
    {
        String UserName = "Icard";//用户名
        String Password = "123";//密码
        try
        { // 加载驱动程序
            Class.forName("net.sourceforge.jtds.jdbc.Driver");
            if (con != null)
            {
                if (con.isClosed() == false)
                {
                    closecon();
                }
            }
            con = DriverManager.getConnection("jdbc:jtds:sqlserver://"+Ipaddress+":1433/Icard", UserName,Password);
        } catch (ClassNotFoundException e) {
            System.out.println("加载驱动程序出错");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    //获取交易记录
    public List getdata(String sql)
    {
        if (con != null)
        {
            try
            {
                if (con.isClosed() == true)
                {
                    lianjie();
                }
            }
            catch (SQLException e)
            {
                e.printStackTrace();
            }
        }
        ArrayList<Map>  list = new ArrayList<Map>();
        Map<Object,Object> map;
        try
        {
//            String sql="SELECT * FROM jiaoyijilu where userID="+userID;
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next())
            {
                map = new HashMap<>();
                map.put("time",formattime.format(rs.getTimestamp(J_time)));
                if (rs.getBoolean(J_io))
                {
                    map.put("io","收入:");
                    map.put("detail","+ " + rs.getString(J_detail));

                }
                else
                {
                    map.put("io","支出:");
                    map.put("detail","- " + rs.getString(J_detail));

                }
                map.put("oldbalance",rs.getFloat(J_oldbalance));
                map.put("id",rs.getInt(J_id));
                map.put("leixing",rs.getString(J_leixing));
                map.put("beizhu",rs.getString(J_beizhu));
                list.add(map);
            }
            rs.close();
            stmt.close();
            closecon();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        return list;
    }

    private boolean closecon()
    {
        if (con != null)
        {
            try
            {
                con.close();
                return true;
            }
            catch (SQLException e)
            {
                Log.w("SQL","数据库连接未正常关闭");
                return false;
            }
        }
        return false;
    }

    public boolean chongfu(String yonghuming)
    {
        if (con != null)
        {
            try
            {
                if (con.isClosed() == true)
                {
                    lianjie();
                }
            }
            catch (SQLException e)
            {
                e.printStackTrace();
            }
        }
        String sql="SELECT * FROM yonghuxinxi where yonghuming = '"+yonghuming+"'";
        Statement stmt = null;
        try
        {
            stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next())
            {
                rs.close();
                stmt.close();
                closecon();
                return true;
            }
            rs.close();
            stmt.close();
            closecon();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        return false;
    }

    public boolean executesql(String sql)
    {
        if (con != null)
        {
            try
            {
                if (con.isClosed() == true)
                {
                    lianjie();
                }
            }
            catch (SQLException e)
            {
                e.printStackTrace();
            }
        }
        Statement stmt = null;
        try
        {
            stmt = con.createStatement();
            int rs = stmt.executeUpdate(sql);
            while (rs > 0)
            {
                stmt.close();
                closecon();
                return true;
            }
            stmt.close();
            closecon();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        return false;
    }

    public boolean yijingzhuce(String openid)
    {
        String yonghuming,mima;
        try
        {
            if (con.isClosed() == true)
            {
                lianjie();
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        String sql = "SELECT * FROM yonghuxinxi where openid = '" + openid + "'";
        Statement stmt = null;
        try
        {
            stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next())
            {
                yonghuming = rs.getString("yonghuming");
                mima = rs.getString("mima");
                rs.close();
                stmt.close();
                closecon();
                Splash.splashthis.db.execSQL("update qq set yonghuming = '"+yonghuming+"' , mima = '"+mima+"' where _id = 1;");
                return true;
            }
            rs.close();
            stmt.close();
            closecon();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        return false;
    }

    public boolean denglu(String yonghuming,String mima)
    {
        while (con == null)
        {
        }
        if (con != null)
        {
            try
            {
                if (con.isClosed() == true)
                {
                    lianjie();
                }
            }
            catch (SQLException e)
            {
                e.printStackTrace();
            }
        }
        String sql="SELECT mima FROM yonghuxinxi where yonghuming = '"+yonghuming+"'";
        Statement stmt = null;
        try
        {
            stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next())
            {
                if (rs.getString("mima").equals(mima))
                {
                    rs.close();
                    stmt.close();
                    closecon();
                    return true;
                }
            }
            rs.close();
            stmt.close();
            closecon();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        return false;
    }

    public String getxinxi(String yonghuming)
    {
        if (con != null)
        {
            try
            {
                if (con.isClosed() == true)
                {
                    lianjie();
                }
            }
            catch (SQLException e)
            {
                e.printStackTrace();
            }
        }
        String sql = "SELECT * FROM yonghuxinxi where yonghuming = '" + yonghuming + "'";
        Statement stmt = null;
        try
        {
            String openid, access_token, expires_in;
            stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next())
            {
                openid = rs.getString("openid");
                access_token = rs.getString("access_token");
                expires_in = rs.getString("expires_in");
                if (!openid.equals("null"))
                {
                    Splash.splashthis.mytencent.setOpenId(openid);
                    Splash.splashthis.mytencent.setAccessToken(access_token,expires_in);
                }else
                {
                    Splash.splashthis.mytencent.setOpenId("0");
                    Splash.splashthis.mytencent.setAccessToken("0","0");
                }
                rs.close();
                stmt.close();
                closecon();
                return "update qq set openid = '" + openid + "' , access_token = '" + access_token + "' , expires_in = '" + expires_in + "' where _id = 1;";
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        return "";
    }

    public List getdata2(String sql)
    {
        if (con != null)
        {
            try
            {
                if (con.isClosed() == true)
                {
                    lianjie();
                }
            }
            catch (SQLException e)
            {
                e.printStackTrace();
            }
        }
        ArrayList<Map>  list = new ArrayList<Map>();
        Map<Object,Object> map;
        try
        {
//            String sql="SELECT * FROM jiaoyijilu where userID="+userID;
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next())
            {
                map = new HashMap<>();
                map.put("yonghuming",rs.getString(D_yonghuming));
                map.put("biaoti",rs.getString(D_biaoti));
                map.put("neirong",rs.getString(D_neirong));
                map.put("shijian",formattime.format(rs.getTimestamp(D_shijian)));
                map.put("id",rs.getString(D_id));
                list.add(map);
            }
            rs.close();
            stmt.close();
            closecon();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        return list;
    }

    public List getdata3(String sql)
    {
        if (con != null)
        {
            try
            {
                if (con.isClosed() == true)
                {
                    lianjie();
                }
            }
            catch (SQLException e)
            {
                e.printStackTrace();
            }
        }
        ArrayList<Map>  list = new ArrayList<Map>();
        Map<Object,Object> map;
        try
        {
//            String sql="SELECT * FROM jiaoyijilu where userID="+userID;
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next())
            {
                map = new HashMap<>();
                map.put("yonghuming",rs.getString(H_yonghuming));
                map.put("neirong",rs.getString(H_neirong));
                map.put("shijian",formattime.format(rs.getTimestamp(H_shijian)));
                list.add(map);
            }
            rs.close();
            stmt.close();
            closecon();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        return list;
    }

    public float getoldbalance(String sql)
    {
        float oldbalance = 0;
        if (con != null)
        {
            try
            {
                if (con.isClosed() == true)
                {
                    lianjie();
                }
            }
            catch (SQLException e)
            {
                e.printStackTrace();
            }
        }
        try
        {
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next())
            {
                oldbalance = rs.getFloat("oldbalance");
            }
            rs.close();
            stmt.close();
            closecon();
        }
        catch (SQLException e)
        {
            e.printStackTrace();

        }
        return oldbalance;
    }
//    public void testConnection(Connection con) throws java.sql.SQLException {
//
//        try {
//
//            String sql = "SELECT * FROM table_test";//查询表名为“table_test”的所有内容
//            Statement stmt = con.createStatement();//创建Statement
//            ResultSet rs = stmt.executeQuery(sql);//ResultSet类似Cursor
//
//            while (rs.next()) {//<code>ResultSet</code>最初指向第一行
//                System.out.println(rs.getString("test_id"));//输出第n行，列名为“test_id”的值
//                System.out.println(rs.getString("test_name"));
//            }
//
//            rs.close();
//            stmt.close();
//        } catch (SQLException e) {
//            System.out.println(e.getMessage().toString());
//        } finally {
//            if (con != null)
//                try {
//                    con.close();
//                } catch (SQLException e) {
//                }
//        }
//    }
}
