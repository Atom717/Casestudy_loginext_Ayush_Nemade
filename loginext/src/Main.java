import java.sql.*;
import java.util.*;
public class Main {
    static String db = "jdbc:mysql://localhost:3306/loginext_case_study?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    static String User = "root";
    static String password = "Ayush@191";

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int N = sc.nextInt();
        int M = sc.nextInt();
        int[][] customers = new int[N][2];
        for (int i = 0; i < N; i++) {
            customers[i][0] = sc.nextInt();
            customers[i][1] = sc.nextInt();
        }

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(db, User, password);
            conn.setAutoCommit(false);
            try {
                PreparedStatement ps = conn.prepareStatement(
                        "INSERT IGNORE INTO drivers(d_id, free_at, status) VALUES (?,0,0)");
                for (int i = 1; i <= M; i++) {
                    ps.setInt(1, i);
                    ps.addBatch();
                }
                ps.executeBatch();
                conn.commit();
            } catch (Exception e) {
                System.out.println("Exception block1");
                System.out.println(e);
            }
            try{
                PreparedStatement ps= conn.prepareStatement("INSERT INTO customers(order_time,travel_time) VALUES (?,?)");
                for(int i=0;i<N;i++){
                    ps.setInt(1,customers[i][0]);
                    ps.setInt(2,customers[i][1]);
                    ps.addBatch();
                }
                ps.executeBatch();
                conn.commit();
            } catch (Exception e) {
                System.out.println("Exception block2");
            }
            for(int i=0;i<N;i++){
                int c_id=i+1;
                int arrival=customers[i][0];
                int travel=customers[i][1];
                PreparedStatement ps1= conn.prepareStatement("UPDATE drivers SET status=0 where free_at<=?");
                ps1.setInt(1,arrival);
                ps1.executeUpdate();

                int d_id=-1;
                PreparedStatement ps2= conn.prepareStatement("select d_id from drivers where status=0 order by d_id limit 1");
                ResultSet result= ps2.executeQuery();
                if(result.next()){
                    d_id=result.getInt(1);
                }
                PreparedStatement ps=conn.prepareStatement("INSERT INTO assignments(customer, driver) VALUES (?,?)");
                if(d_id==-1){
                    ps.setInt(1,c_id);
                    ps.setNull(2,Types.INTEGER);
                    ps.executeUpdate();
                    System.out.println("C" + c_id + " - No Food :-(");
                }
                else{
                    int newFree=arrival+travel;
                    PreparedStatement upd=conn.prepareStatement("update drivers set free_at=?,status=1 where d_id=? ");
                    upd.setInt(1,newFree);
                    upd.setInt(2,d_id);
                    upd.executeUpdate();
                    ps.setInt(1,c_id);
                    ps.setInt(2,d_id);
                    ps.executeUpdate();
                    System.out.println("C" + c_id + " - D"+d_id);
                }
                conn.commit();
            }

            // Clear previous data

        } catch (Exception e) {
            System.out.println("couldn't connect");
            System.out.println(e);
        }
    }
}