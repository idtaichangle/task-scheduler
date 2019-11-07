package com.cvnavi.ais.servlet;

/**
 * Servlet implementation class ShipInfoServlet
 */

public class TrackServlet {
	private static final long serialVersionUID = 1L;


//	protected void doGet(HttpServletRequest request, HttpServletResponse response)
//			throws ServletException, IOException {
//		response.setContentType("application/json");
//		response.setCharacterEncoding("UTF-8");
//
//		boolean success = true;
//
//		String mmsi = request.getParameter("mmsi");
//		String btime = request.getParameter("btime");
//		String etime = request.getParameter("etime");
//		String enc=request.getParameter("enc");
//		if(enc==null){//默认要压缩输出
//			enc="1";
//		}
//
//		if (mmsi == null || mmsi.length() != 9) {
//			success = false;
//		}
//		long p1=0,p2=0;
//
//		try{
//			p1=Long.parseLong(btime);
//			p2=Long.parseLong(etime);
//		}catch(NumberFormatException ex){
//			success = false;
//		}
//
//		PrintWriter out = response.getWriter();
//		if(!success){
//			out.println("{\"success\":false,\"message\":\"参数错误。\"}");
//		}else{
//			List<Track> track = ShipxyService.getTrack(mmsi,p1,p2);
//			if(track==null || track.size()==0){
//				track = MyshipsService.getTrack(mmsi,p1,p2);
//			}
//
//			String output="";
//
//			if (track!=null && track.size()>0) {
//				ObjectMapper mapper = new ObjectMapper();
//				String s = mapper.writer((PrettyPrinter)null).writeValueAsString(track);
//				output="{\"success\":true,\"message\":\"操作成功。\",\"data\":" + s + "}";
//			} else {
//				output="{\"success\":false,\"message\":\"操作失敗。\"}";
//			}
//			if("1".equals(enc)){
//				byte[]b=output.getBytes();
//				b=ZipUtil.gZip(b);
//				output=Base64.getEncoder().encodeToString(b);
//			}
//
//			out.println(output);
//		}
//	}


}
