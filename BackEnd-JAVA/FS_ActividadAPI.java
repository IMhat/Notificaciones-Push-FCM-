package cl.mpsoft.txm.api.rest.v10;



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
import cl.mpsoft.txm.dao.CheckList_Submit;
import cl.mpsoft.txm.dao.Concepto;
import cl.mpsoft.txm.dao.Direccion;
import cl.mpsoft.txm.dao.FS_Actividad;
import cl.mpsoft.txm.dao.FS_Participantes;
import cl.mpsoft.txm.dao.Grupo;
import cl.mpsoft.txm.dao.NotificationPushDAO;
import cl.mpsoft.txm.dao.Usuario;
import cl.mpsoft.txm.dao.Usuario_Estado;
import cl.mpsoft.txm.utils.CheckDates;
import cl.mpsoft.txm.utils.LogManager;
import cl.mpsoft.txm.utils.UtilParser;
import cl.mpsoft.txm.vo.CheckList_SubmitVO;
import cl.mpsoft.txm.vo.ConceptoVO;
import cl.mpsoft.txm.vo.DireccionVO;
import cl.mpsoft.txm.vo.FS_ActividadVO;
import cl.mpsoft.txm.vo.FS_ParticipantesVO;
import cl.mpsoft.txm.vo.GrupoVO;
import cl.mpsoft.txm.vo.UsuarioVO;
import cl.mpsoft.txm.vo.Usuario_EstadoVO;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;

@Path("/v10/fsactividad")
public class FS_ActividadAPI extends APISec implements IAPI<FS_ActividadVO> {
	
	CheckDates cd = new CheckDates();

	@POST
	@Path("/add")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response add(FS_ActividadVO t,@Context HttpHeaders headers) {
		System.out.println("entra a add ap");
		
	    // Crear una instancia de NotificationPushDAO
		NotificationPushDAO notificationPushDAO = new NotificationPushDAO(getCompany(headers));	
		
		// TODO Auto-generated method stub
		MessageResponse mR = new MessageResponse();
		FS_Actividad c = new FS_Actividad(getCompany(headers));
		String horadesde = t.getFECHAYHORAPLANIFICACIONDESDE() + ":00";
		String horahasta = t.getFECHAYHORAPLANIFICACIONHASTA()+ ":00";
//		if(!cd.checkDate(horadesde, horahasta)) {
//			mR.setCode(422);
//			mR.setMessage("Hay un error en las fechas ingresadas");
//			mR.setData(null);
//			return Response.status(422).entity(mR).build();
//		}
//		List<FS_ActividadVO> actList = c.selectBy(" where date(fs_actividad.FECHAYHORAPLANIFICACIONDESDE) = date(?) "
//				+ "									AND fs_actividad.IDUSUARIOASIGNADO = ? ",
//														new Object[] {t.getFECHAYHORAPLANIFICACIONDESDE(), t.getIDUSUARIOASIGNADO()});
//		if(!actList.isEmpty()) {
//			for (FS_ActividadVO act : actList) {
//				if(!cd.checkDateRange(act.getFECHAYHORAPLANIFICACIONDESDE(), act.getFECHAYHORAPLANIFICACIONHASTA(), 
//										horadesde, horahasta)) {
//					mR.setCode(422);
//					mR.setMessage("Conflicto de tiempo");
//					mR.setData(null);
//					return Response.status(422).entity(mR).build();
//				}
//			}
//		}
		if(t.getIDTIPO() == null) {
			t.setIDPROYECTO(null);
			t.setIDCASO(null);
		} else {
			if(t.getIDTIPO() == 1) {
				t.setIDPROYECTO(null);
			}
			if(t.getIDTIPO() == 2) {
				t.setIDCASO(null);
			}
		}
		t.setIDCREADOR(getIDUser(headers));
		int i = c.save(t);
		mR.setCode(200);
		t.setID(i);
		//Agregamos la Direccion
//		if(t.getDIRECCION()  == 1) {
				Direccion dir = new Direccion(getCompany(headers));
				//DireccionVO nuevDir = t.getDIRECCION();
				DireccionVO nuevDir = new DireccionVO();
				nuevDir.setIDACTIVIDAD(i);				
				nuevDir.setTEXTODIRECCION(t.getDIRECCION().getTEXTODIRECCION());
				//int j = dir.save(nuevDir);
				//nuevDir.setID(j);
				dir.save(nuevDir);
//		}
				
	
		        
		if(t.getPARTICIPANTES() != null && !t.getPARTICIPANTES().isEmpty()) {
			

	        
			FS_Participantes par = new FS_Participantes(getCompany(headers));
			for (Integer idparticipantes : t.getPARTICIPANTES()) {
				FS_ParticipantesVO participante = new FS_ParticipantesVO();
				participante.setIDACTIVIDAD(i);
				participante.setIDUSUARIO(idparticipantes);
				par.save(participante);
				
				
				// Utilizar la instancia de NotificationPushDAO para obtener el token
	            String deviceToken = notificationPushDAO.obtenerTokenDelDispositivo(idparticipantes);
		        
	            try {
	                if (deviceToken != null) {
	                    Message fcmMessage = Message.builder()
	                            .setToken(deviceToken)
	                            .putData("title", "Nueva participacion")
	                            .putData("message", "Tienes una nueva participacion asignada.")
	                            .build();

	                    FirebaseMessaging.getInstance().send(fcmMessage);
	                }
	            } catch (FirebaseMessagingException e) {
	                
	                System.err.println("Error al enviar la notificación: " + e.getMessage());
	            }
			}
		}
		LogManager.logAud(getCompany(headers), "fsactividades", "Actividad Creada", i, getIDUser(headers));
		
		
		// Utilizar la instancia de NotificationPushDAO para obtener el token
        String deviceToken = notificationPushDAO.obtenerTokenDelDispositivo(t.getIDUSUARIOASIGNADO());
        
        try {
            if (deviceToken != null) {
                Message fcmMessage = Message.builder()
                        .setToken(deviceToken)
                        .putData("title", "Nueva actividad asignada")
                        .putData("message", "Tienes una nueva actividad asignada.")
                        .build();

                FirebaseMessaging.getInstance().send(fcmMessage);
            }
        } catch (FirebaseMessagingException e) {
            
            System.err.println("Error al enviar la notificación: " + e.getMessage());
        }

		
		return this.getby(t.getID(), headers);
		// Busco la actividad completa y la retorno
		/*List<FS_ActividadVO> actList = c.selectBy(" where fs_actividad.ID = ? ",
														new Object[] {t.getID()});
	
		if(actList.size() > 0) {
			t = actList.get(0);
		}
		
		mR.setData(t);
		return Response.status(200).entity(mR).build();*/
	}

	@GET
	@Path("/get")
	@Produces(MediaType.APPLICATION_JSON)
	public Response get(@DefaultValue("") @QueryParam("fecha") String fecha,
		  				@DefaultValue("0") @QueryParam("idcaso") Integer idcaso,
	  					@DefaultValue("0") @QueryParam("sinasignar") Integer sinasignar,
  						@DefaultValue("0") @QueryParam("idproyecto") Integer idproyecto,
						@DefaultValue("0") @QueryParam("idusuario") Integer idusuario,
						@DefaultValue("0") @QueryParam("idcontacto") Integer idcontacto,
						@DefaultValue("0") @QueryParam("idcuenta") Integer idcuenta,
						@DefaultValue("0") @QueryParam("calendario") Integer calendario,
						@Context HttpHeaders headers) {
		// TODO Auto-generated method stub
		if(calendario == 0){
			MessageResponse mR = new MessageResponse();
			FS_Participantes par = new FS_Participantes(getCompany(headers));
			FS_Actividad c = new FS_Actividad(getCompany(headers));
			Direccion dir = new Direccion(getCompany(headers));
			List<FS_ActividadVO> actList = new ArrayList<>();
			//By date or User & Date
			if (!fecha.isEmpty() && (sinasignar == 0)) {
				if(idusuario > 0) {
					actList = c.selectBy(" where ? BETWEEN date(fs_actividad.FECHAYHORAPLANIFICACIONDESDE) AND  "
							+"                 date(fs_actividad.FECHAYHORAPLANIFICACIONHASTA) AND  "
							+	" ( fs_actividad.IDUSUARIOASIGNADO = ? or "
							+ " fs_actividad.ID in ("
							+ "    select fs_participantes.IDACTIVIDAD "
							+ "		from fs_participantes"
							+ " 	where fs_participantes.IDUSUARIO = ? )"
							+ " )"
							+ " order by fs_actividad.FECHAYHORAPLANIFICACIONDESDE asc ", new Object[] {fecha, idusuario, idusuario});

					// traer las actividades que soy participante

				}
				else {
					actList = c.selectBy(" where ? BETWEEN date(fs_actividad.FECHAYHORAPLANIFICACIONDESDE) AND "
							+"                 date(fs_actividad.FECHAYHORAPLANIFICACIONHASTA) AND  "
							+ " fs_actividad.IDUSUARIOASIGNADO IS NOT NULL "
							+ " order by fs_actividad.FECHAYHORAPLANIFICACIONDESDE asc ", new Object[]{fecha});
				}

			}
			//By User
			else if(idusuario > 0) {
				actList = c.selectBy(" where fs_actividad.IDUSUARIOASIGNADO = ? ", new Object[]{idusuario});
			}
			//By ID Caso
			else if(idcaso > 0) {
				actList = c.selectBy(" where fs_actividad.IDCASO = ? ", new Object[]{idcaso});
			}
			//By ID Proyecto
			else if(idproyecto > 0) {
				actList = c.selectBy(" where `fs_actividad`.`IDPROYECTO` = ? ", new Object[] {idproyecto});
			}
			//By SinAsignar
			else if(sinasignar > 0) {
				actList = c.selectBy(" where `fs_actividad`.`IDUSUARIOASIGNADO` IS NULL AND date(fs_actividad.FECHAYHORAPLANIFICACIONDESDE) = ? ", new Object[] {fecha});

			}
			else if(idcontacto > 0) {
				actList = c.selectBy(" where `fs_actividad`.`IDCONTACTO` = ? ", new Object[] {idcontacto});

			}else if(idcuenta > 0) {
				actList = c.selectBy(" where `contacto`.`IDCUENTA` = ? ", new Object[] {idcuenta});

			}
			else{
				actList = c.selectBy(null, null);
			}
			//Get the addresses respectively
			for (FS_ActividadVO fs_ActividadVO : actList) {
				List<DireccionVO> listDirecciones = dir.selectBy(" where direccion.IDACTIVIDAD = ? ", new Object[] {fs_ActividadVO.getID()});
				for (DireccionVO direccionVO : listDirecciones) {
					fs_ActividadVO.setDIRECCION(direccionVO);
				}
			}
			// Get the participants AND the status of the assigned users
			if(!actList.isEmpty()) {
				for (FS_ActividadVO fs_ActividadVO : actList) {
					List<FS_ParticipantesVO> listPar = par.selectBy(" where fs_participantes.IDACTIVIDAD = ? ",
							new Object[] {fs_ActividadVO.getID()});
					List<Integer> participantes = new ArrayList<Integer>();
					List<String> listNombresParticipantes = new ArrayList<>();
					if(!listPar.isEmpty()) {
						for (FS_ParticipantesVO listpar : listPar) {
							participantes.add(listpar.getIDUSUARIO());
							listNombresParticipantes.add(listpar.getIDUSUARIODESC());
						}
						fs_ActividadVO.setPARTICIPANTES(participantes);
						fs_ActividadVO.setPARTICIPANTESNOMBRES(listNombresParticipantes);

					}
					String desde = fs_ActividadVO.getFECHAYHORAPLANIFICACIONDESDE();
					String hasta = fs_ActividadVO.getFECHAYHORAPLANIFICACIONHASTA();
					desde = UtilParser.dts(UtilParser.s2dtsb(desde));
					hasta = UtilParser.dts(UtilParser.s2dtsb(hasta));
					fs_ActividadVO.setFECHAYHORAPLANIFICACIONDESDE(desde);
					fs_ActividadVO.setFECHAYHORAPLANIFICACIONHASTA(hasta);
					if(fs_ActividadVO.getIDUSUARIOASIGNADO() != null) {
						Usuario_Estado ue = new Usuario_Estado(getCompany(headers));
						Concepto conc = new Concepto(getCompany(headers));
						List<Usuario_EstadoVO> listConcept = ue.selectBy(" where usuario_estado.IDUSUARIO = ? ORDER BY usuario_estado.FHCAMBIOESTADO DESC LIMIT 1 ", new Object[] {fs_ActividadVO.getIDUSUARIOASIGNADO()});
						for (Usuario_EstadoVO usuario_EstadoVO : listConcept) {
							List<ConceptoVO> listCons = conc.selectBy(" where concepto.ID = ? ", new Object[] {usuario_EstadoVO.getIDCONCEPTO()});
							fs_ActividadVO.setESTADOUSUARIO(listCons.get(0).getTIPO());
						}
					}
				}
			}
			mR.setCode(200);
			mR.setData(actList);
			return Response.status(200).entity(mR).build();
		} else {
			MessageResponse mR = new MessageResponse();
			FS_Actividad c = new FS_Actividad(getCompany(headers));
			List<FS_ActividadVO> actList = new ArrayList<>();
			actList = c.selectBy(" where fs_actividad.IDUSUARIOASIGNADO = ? ", new Object[]{idusuario});
			mR.setCode(200);
			mR.setData(actList);
			return Response.status(200).entity(mR).build();
		}

	}
	
	@Override
	@GET
	@Path("/getby")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getby(@QueryParam("id") Integer id,@Context HttpHeaders headers) {
		// TODO Auto-generated method stub
		MessageResponse mR = new MessageResponse();
		FS_Actividad c = new FS_Actividad(getCompany(headers));
		Direccion dir = new Direccion(getCompany(headers));
		FS_Participantes par = new FS_Participantes(getCompany(headers));
		mR.setCode(200);
		List<FS_ActividadVO> actList = c.selectBy(" where fs_actividad.ID = ? ", new Object[]{id});
		if(!actList.isEmpty()) {
			FS_ActividadVO act = new FS_ActividadVO();
			act = actList.get(0);
			List<DireccionVO> listDirecciones = dir.selectBy(" where direccion.IDACTIVIDAD = ? ", 
																	new Object[] {act.getID()});
			if(listDirecciones.size() > 0) {
				act.setDIRECCION(listDirecciones.get(0));
			}
			
			List<FS_ParticipantesVO> listParticipantes = par.selectBy(" where fs_participantes.IDACTIVIDAD = ? ", 
																			new Object[] {act.getID()});
			List<Integer> listIDParticipantes = new ArrayList<>();
			List<String> listNombresParticipantes = new ArrayList<>();
			for (FS_ParticipantesVO lP : listParticipantes) {
				listIDParticipantes.add(lP.getIDUSUARIO());
				listNombresParticipantes.add(lP.getIDUSUARIODESC());
			}
			act.setPARTICIPANTES(listIDParticipantes);
			act.setPARTICIPANTESNOMBRES(listNombresParticipantes);
			mR.setData(act);
			return Response.status(200).entity(mR).build();
		}
		mR.setData(null);
		return Response.status(200).entity(mR).build();
	}
	
	@Override
	@PUT
	@Path("/upd")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response upd(FS_ActividadVO t,@Context HttpHeaders headers) {
		// TODO Auto-generated method stub
		MessageResponse mR = new MessageResponse();
		FS_Actividad c = new FS_Actividad(getCompany(headers));
		String desde = t.getFECHAYHORAPLANIFICACIONDESDE();
		String hasta = t.getFECHAYHORAPLANIFICACIONHASTA();
		desde = UtilParser.dts(UtilParser.s2dtsb(desde));
		hasta = UtilParser.dts(UtilParser.s2dtsb(hasta));
		t.setFECHAYHORAPLANIFICACIONDESDE(desde);
		t.setFECHAYHORAPLANIFICACIONHASTA(hasta);
		String horadesde = t.getFECHAYHORAPLANIFICACIONDESDE() + ":00";
		String horahasta = t.getFECHAYHORAPLANIFICACIONHASTA()+ ":00";
//		if(!cd.checkDate(horadesde, horahasta)) {
//			mR.setCode(422);
//			mR.setMessage("Hay un error en las fechas ingresadas");
//			mR.setData(null);
//			return Response.status(200).entity(mR).build();
//		}
//		List<FS_ActividadVO> actList = c.selectBy(" where date(fs_actividad.FECHAYHORAPLANIFICACIONDESDE) = date(?) "
//				+ "									AND fs_actividad.IDUSUARIOASIGNADO = ? AND fs_actividad.ID <> ? ",
//														new Object[] {t.getFECHAYHORAPLANIFICACIONDESDE(), t.getIDUSUARIOASIGNADO(),
//																t.getID()});
//		if(!actList.isEmpty()) {
//			for (FS_ActividadVO act : actList) {
//				if(!cd.checkDateRange(act.getFECHAYHORAPLANIFICACIONDESDE(), act.getFECHAYHORAPLANIFICACIONHASTA(), 
//						t.getFECHAYHORAPLANIFICACIONDESDE(), t.getFECHAYHORAPLANIFICACIONHASTA())) {
//					mR.setCode(422);
//					mR.setMessage("Conflicto de tiempo");
//					mR.setData(null);
//					return Response.status(200).entity(mR).build();
//				}
//			}
//		}
		if(t.getIDTIPO() == null) {
			t.setIDPROYECTO(null);
			t.setIDCASO(null);
		} else {
			if(t.getIDTIPO() == 1) {
				t.setIDPROYECTO(null);
			}
			if(t.getIDTIPO() == 2) {
				t.setIDCASO(null);
			}
		}
		//Agregamos la Direccion
		//if(t.getIDTIPO() == 1) {
		Direccion dir = new Direccion(getCompany(headers));
		
		if(t.getDIRECCION().getID() != null && t.getDIRECCION().getID() < 0) {
			t.getDIRECCION().setID(0);
		}
		DireccionVO nuevDir = t.getDIRECCION();
		nuevDir.setIDACTIVIDAD(t.getID());
		int j = dir.save(nuevDir);
		nuevDir.setID(j);
		//Borro todas las otras direcciones asociadas a la actividad
		List<DireccionVO> direcciones = dir.selectBy(" where direccion.IDACTIVIDAD = ? and direccion.ID <> ? ", 
													new Object[] {t.getID(),nuevDir.getID()
													});
		for (DireccionVO direccionVO : direcciones) {
			dir.delete(direccionVO);
		}
		
		//}
		LogManager.logAud(getCompany(headers), "fsactividades", "Actividad Actualizada", t.getID(), getIDUser(headers));
		//Delete all participants in the activity and re-add them (In case they have been changed or deleted)
		FS_Participantes par = new FS_Participantes(getCompany(headers));
		List<FS_ParticipantesVO> listPar = par.selectBy(" where fs_participantes.IDACTIVIDAD = ? ", new Object[] {t.getID()});
		for (FS_ParticipantesVO fs_ParticipantesVO : listPar) {
			par.delete(fs_ParticipantesVO);
		}
		if(t.getPARTICIPANTES() != null && !t.getPARTICIPANTES().isEmpty()) {
			for (Integer idparticipantes : t.getPARTICIPANTES()) {
				FS_ParticipantesVO participante = new FS_ParticipantesVO();
				participante.setIDACTIVIDAD(t.getID());
				participante.setIDUSUARIO(idparticipantes);
				par.save(participante);
			}
		}
		c.update(t);
		mR.setCode(200);
		
		return this.getby(t.getID(), headers);
		
		/*
		// Busco la actividad completa y la retorno
		List<FS_ActividadVO> actList = c.selectBy(" where fs_actividad.ID = ? ",
														new Object[] {t.getID()});
	
		if(actList.size() > 0) {
			t = actList.get(0);
		}
		
		mR.setData(t);
		return Response.status(200).entity(mR).build();*/
	}

	@Override
	@DELETE
	@Path("/del")
	@Produces(MediaType.APPLICATION_JSON)
	public Response del(@QueryParam("id") Integer id,@Context HttpHeaders headers) {
		// TODO Auto-generated method stub
		MessageResponse mR = new MessageResponse();
		FS_ActividadVO t = new FS_ActividadVO();
		t.setID(id);
		FS_Actividad c = new FS_Actividad(getCompany(headers));
		c.delete(t);
		mR.setCode(200);
		List<FS_ActividadVO> listCuentas = c.selectBy(" where fs_actividad.ID = ? ", new Object[] {id});
		if(listCuentas.isEmpty()) {
			mR.setData(t);
			LogManager.logAud(getCompany(headers), "fsactividades", "Actividad Eliminada", id, getIDUser(headers));
			return Response.status(200).entity(mR).build();
		}
		mR.setData(null);
		mR.setCode(500);
		return Response.status(500).entity(mR).build();
	}
	
	@GET
	@Path("/ejecutivos/get")
	@Produces(MediaType.APPLICATION_JSON)
	public Response eject(@Context HttpHeaders headers){
		MessageResponse mR = new MessageResponse();
		Grupo gr = new Grupo(getCompany(headers));
		Usuario us = new Usuario(getCompany(headers));
		List<UsuarioVO> usuarios = us.selectBy(" where " + // inner join permisos on usuario.IDROL = permisos.IDROL "+
													" usuario.ID NOT IN (1,2)", null);
		List<GrupoVO> groups = gr.selectBy(null, null);
		for (GrupoVO grupoVO : groups) {
			List<UsuarioVO> usuarioss = new ArrayList<UsuarioVO>();
			for (UsuarioVO usuarioVO : usuarios) {
				if(usuarioVO.getIDGRUPO() == grupoVO.getID()) {
					usuarioVO.setNOMBRE(usuarioVO.getNOMBRE()+ " " + usuarioVO.getAPELLIDO());
					usuarioss.add(usuarioVO);
					grupoVO.setGRUPOUSUARIOS(usuarioss);
				}
			}
		}
		mR.setCode(200);
		mR.setData(groups);
		return Response.status(200).entity(mR).build();
	}
	
	@POST
	@Path("/done")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response fav(CheckList_SubmitVO t,@Context HttpHeaders headers) {
		// TODO Auto-generated method stub
		MessageResponse mR = new MessageResponse();
		CheckList_Submit c = new CheckList_Submit(getCompany(headers));
		t.setIDUSUARIO(getIDUser(headers));
		c.save(t);
		mR.setCode(200);
		mR.setData(t);
		return Response.status(200).entity(mR).build();
	}
	
	@POST
	@Path("/undone")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response unfav(CheckList_SubmitVO t,@Context HttpHeaders headers) {
		// TODO Auto-generated method stub
		MessageResponse mR = new MessageResponse();
		CheckList_Submit c = new CheckList_Submit(getCompany(headers));
		t.setIDUSUARIO(getIDUser(headers));
		c.delete(t);
		mR.setCode(200);
		mR.setData(t);
		return Response.status(200).entity(mR).build();
	}
	
}
