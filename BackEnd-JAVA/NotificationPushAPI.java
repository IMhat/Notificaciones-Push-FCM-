package cl.mpsoft.txm.api.rest.v10;
import cl.mpsoft.txm.vo.NotificationPushVO;

import java.io.IOException;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import cl.mpsoft.txm.api.rest.messages.MessageResponse;
import cl.mpsoft.txm.api.sec.APISec;
import cl.mpsoft.txm.dao.Caso;


import cl.mpsoft.txm.dao.NotificationPushDAO;
import cl.mpsoft.txm.dao.PR_Parametros;
import cl.mpsoft.txm.dao.Roles;
import cl.mpsoft.txm.vo.CasoVO;

import javax.ws.rs.PathParam;

import cl.mpsoft.txm.vo.RolesVO;
import cl.mpsoft.txm.vo.UsuarioVO;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.MulticastMessage;

/* import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
*/


@Path("/v10/notification-push")
public class NotificationPushAPI extends APISec implements IAPI<NotificationPushVO> {
	

	
	public void initializeFirebaseAdminSDK() {
	    try {
	        System.out.println("se llamó a Firebase");
	        FileInputStream serviceAccount = new FileInputStream("C:\\Users\\Mateo Mansilla\\Documents\\TrackingMove-Software\\be\\mp_mysupersonic_be\\txm\\src\\google-services.json");

	        FirebaseOptions options = FirebaseOptions.builder()
	                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
	                .build();

	        FirebaseApp.initializeApp(options);
	    } catch (IOException e) {
	        System.err.println("Error al leer el archivo de configuración: " + e.getMessage());
	    }
	}

	

    public void sendNotificationToUser(String deviceToken, String message) {
        Message fcmMessage = Message.builder()
                .setToken(deviceToken)
                .putData("message", message)
                .build();

        try {
            FirebaseMessaging.getInstance().send(fcmMessage);
        } catch (FirebaseMessagingException e) {
            System.err.println("Error al enviar la notificación: " + e.getMessage());
        }
    }

    @POST
    @Path("/add-token")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
	public Response add(NotificationPushVO deviceToken, HttpHeaders headers) {
		// TODO Auto-generated method stub
        MessageResponse mR = new MessageResponse();
        NotificationPushDAO dao = new NotificationPushDAO(getCompany(headers));
        int irs = dao.insert(deviceToken); 
        if (irs > 0) {
            mR.setCode(200);
            mR.setMessage("Token added successfully.");
        } else {
            mR.setCode(500);
            mR.setMessage("Failed to add token.");
        }
        return Response.status(mR.getCode()).entity(mR).build();
	}
	

	@Override
	public Response getby(Integer id, HttpHeaders headers) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Response upd(NotificationPushVO t, HttpHeaders headers) {
		// TODO Auto-generated method stub
		return null;
	}

	@DELETE
	@Path("/del-token/{userId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response del(@PathParam("userId") Integer userId, HttpHeaders headers) {
		// TODO Auto-generated method stub
	    MessageResponse mR = new MessageResponse();
	    NotificationPushDAO dao = new NotificationPushDAO(getCompany(headers));

	    int irs = dao.deleteByUserId(userId);
	    if (irs > 0) {
	        mR.setCode(200);
	        mR.setMessage("Tokens for user deleted successfully.");
	    } else {
	        mR.setCode(500);
	        mR.setMessage("Failed to delete tokens for user.");
	    }
	    return Response.status(mR.getCode()).entity(mR).build();
	}

	
	
/*
    @POST
    @Path("/add-token")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addToken(NotificationPushVO deviceToken, @Context HttpHeaders headers) {
        MessageResponse mR = new MessageResponse();
        NotificationPushDAO dao = new NotificationPushDAO(getCompany(headers));
        int irs = dao.insert(deviceToken); 
        if (irs > 0) {
            mR.setCode(200);
            mR.setMessage("Token added successfully.");
        } else {
            mR.setCode(500);
            mR.setMessage("Failed to add token.");
        }
        return Response.status(mR.getCode()).entity(mR).build();
    }
  ¨*/

    
    @DELETE
    @Path("/delete-token/{userId}/{token}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteToken(@PathParam("userId") int userId, @PathParam("token") String token, @Context HttpHeaders headers) {
    	
        MessageResponse mR = new MessageResponse();
        NotificationPushDAO dao = new NotificationPushDAO(getCompany(headers));
        
        NotificationPushVO deviceToken = new NotificationPushVO();
        deviceToken.setUserId(userId);
        deviceToken.setToken(token);

        int irs = dao.delete(deviceToken);
        if (irs > 0) {
            mR.setCode(200);
            mR.setMessage("Token deleted successfully.");
        } else {
            mR.setCode(500);
            mR.setMessage("Failed to delete token.");
        }
        return Response.status(mR.getCode()).entity(mR).build();
    }
    
    
}


