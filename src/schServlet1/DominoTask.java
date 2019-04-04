package schServlet1;

import java.util.TimerTask;
import java.util.Date;
import lotus.domino.*;

public class DominoTask extends TimerTask {
	Thread t = null;
	Date now;
	long howLong;
	String dbName = "Notesdata\\Sandbox\\Scheduled Servlets in\\Servlets.nsf";
	String formName = "ServletCheck";
	String viewName = "ServletSetup";
	String servletName = "DominoTimer";

	public void run() {
		// As is the case with all classes that extend Thread or implement Runnable,
		// they must override the public void run() method.
		try {
			now = new Date();
			System.out.println(servletName + ": " + now.toString());
			System.out.println("Threads #: " + t.activeCount());
			NotesThread.sinitThread();
			Session session = NotesFactory.createSession();
			Database db = session.getDatabase(null, dbName);
			Document doc = db.createDocument();
			doc.appendItemValue("Form", formName);
			doc.appendItemValue("ServletName", servletName);
			doc.appendItemValue("StartTime", now.toString());
			doc.save(true, false);
			View view = db.getView(viewName);
			Document doc1 = view.getDocumentByKey(servletName, true);
			howLong = new Long(doc1.getItemValueInteger("SleepTime_d")).longValue();
			if (howLong < 0) {
				this.cancel();
			}
			/* Recycle objects */
			doc.recycle();
			doc1.recycle();
			view.recycle();
			NotesThread.stermThread();
			System.runFinalization();
		} catch (Exception e) {
			System.out.println("Error: " + e.toString());
		}
	}
}
