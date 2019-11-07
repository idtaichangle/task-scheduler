package com.cvnavi.ais.servlet;

/**
 * Servlet implementation class ShipInfoServlet
 */

public class ShipInfoServlet  {
	private static final long serialVersionUID = 1L;

//	protected void doGet(HttpServletRequest request, HttpServletResponse response)
//			throws ServletException, IOException {
//		response.setContentType("application/json");
//		response.setCharacterEncoding("UTF-8");
//
//		String mmsi = request.getParameter("mmsi");
//		List<Ship> ship = ShipxyService.getShip(mmsi);
//		if (ship==null || ship.size()==0) {
//			ship = MyshipsService.getShip(mmsi);
//		}
//
//		PrintWriter out = response.getWriter();
//		if (ship!=null && ship.size()>0) {
//			ObjectMapper mapper = new ObjectMapper();
//			String s=mapper.writerWithDefaultPrettyPrinter().writeValueAsString(ship);
//
//			out.println("{\"success\":true,\"message\":\"操作成功。\",\"data\":"+s+"}");
//		} else {
//			out.println("{\"success\":false,\"message\":\"操作失敗。\"}");
//		}
//	}
}
