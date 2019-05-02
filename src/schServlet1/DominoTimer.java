package schServlet1;

import javax.servlet.*;
import javax.servlet.http.*;
import java.util.Timer;
import lotus.domino.*;

public class DominoTimer extends HttpServlet {
	String dbName = "Test/Servlets.nsf";
	String formName = "ServletCheck";
	String viewName = "ServletSetup";
	String servletName = "ScheduledServlet";
	long howLong;

	public void init(ServletConfig config) throws ServletException {
		super.init(config);
	}

	protected void processRequest(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, java.io.IOException {
		response.setContentType("text/html");
		java.io.PrintWriter out = response.getWriter();
		out.println("Starting Domino Timer");
		Timer timer = new Timer(false);
		DominoTask dtask = new DominoTask();
		timer.schedule(dtask, 0, processNotes());
		out.close();

		System.out.println("hello");
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, java.io.IOException {
		this.processRequest(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, java.io.IOException {
		processRequest(request, response);
	}

	private long processNotes() {
		try {
			NotesThread.sinitThread();
			Session session = NotesFactory.createSession();
			Database db = session.getDatabase(null, dbName);
			/* Get current wait time */
			View view = db.getView(viewName);
			Document doc1 = view.getDocumentByKey(servletName, true);
			howLong = new Long(doc1.getItemValueInteger("SleepTime_d")).longValue();
			/* Recycle objects */
			doc1.recycle();
			view.recycle();
			NotesThread.stermThread();
			System.runFinalization();
			System.gc();
		} catch (Exception notex) {
			notex.printStackTrace();
			System.err.println("Error: " + notex.toString());
			System.err.println("Error: " + notex.getMessage());
		}
		return howLong;
	}
}
