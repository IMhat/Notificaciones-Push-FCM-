package cl.mpsoft.txm.dao;



import cl.mpsoft.txm.vo.NotificationPushVO;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import cl.mpsoft.txm.db.Query;

public class NotificationPushDAO extends DAO<NotificationPushVO> {



    public NotificationPushDAO(String companyName) {
        super(companyName);
    }


    
	@Override
	public int insert(NotificationPushVO notificationPush) {
		Query query = new Query();
		ResultSet rs = null;
		int irs = 0;
		try {
			query.open (this.getCompanyName());
			String insert = "INSERT INTO tokens_push (user_id, token) VALUES (?, ?)";
			irs = query.execute(insert, new Object[]{ 
					notificationPush.getUserId(),
					notificationPush.getToken()
									});
			 
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {
			try {
				if(rs != null){
					rs.close();
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				query.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return irs;

	}

    
	@Override
	public int delete(NotificationPushVO notificationPush) {
		Query query = new Query();
		ResultSet rs = null;
		int irs = 0;
		try {
			query.open (this.getCompanyName());
			String delete = "DELETE FROM tokens_push WHERE user_id = ? AND token = ?";
							
			irs = query.execute(delete, new Object[]{
					notificationPush.getUserId(),
					notificationPush.getToken()
									});
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {
			try {
				if(rs != null){
					rs.close();
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				query.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return irs;
	}
	
	public int deleteByUserId(int userId) {
	    Query query = new Query();
	    ResultSet rs = null;
	    int irs = 0;
	    try {
	        query.open(this.getCompanyName());
	        String delete = "DELETE FROM tokens_push WHERE user_id = ?";
	        irs = query.execute(delete, new Object[]{userId});
	    } catch (SQLException e) {
	        e.printStackTrace();
	    } finally {
	        try {
	            if (rs != null) {
	                rs.close();
	            }
	            query.close();
	        } catch (SQLException e) {
	            e.printStackTrace();
	        }
	    }
	    return irs;
	}


	public String obtenerTokenDelDispositivo(int idUsuario) {
	    Query query = new Query();
	    ResultSet rs = null;
	    String token = null;
	    String select = "SELECT token FROM tokens_push WHERE user_id = ?";
	    
	    try {
	        query.open(this.getCompanyName());
	        rs = query.select(select, new Object[] { idUsuario }); 
	        if (rs.next()) {
	            token = rs.getString("token");
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    } finally {
	        try {
	            if (rs != null) {
	                rs.close();
	            }
	            query.close();
	        } catch (SQLException e) {
	            e.printStackTrace();
	        }
	    }

	    return token;
	}

	@Override
	public List<NotificationPushVO> selectBy(String clause, Object[] args) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int update(NotificationPushVO t) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int save(NotificationPushVO t) {
		// TODO Auto-generated method stub
		return 0;
	}

}

