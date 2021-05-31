package servlet; 
 
import java.io.IOException; 
import java.util.Calendar; 
import javax.servlet.ServletException; 
import javax.servlet.http.HttpServletRequest; 
import javax.servlet.http.HttpServletResponse; 
import entity.ChatMessage; 
import entity.ChatUser; 
 
public class NewMessageServlet extends ChatServlet { 
 
	private static final long serialVersionUID = 1L; 
 
  protected void doPost(HttpServletRequest request, HttpServletResponse 
		  response) throws ServletException, IOException { 
	  // �� ��������� ������������ ��������� ISO-8859. ��� ��� ��  
	  // �������� ������ � ��������� UTF-8 
	  // �� ���������� ���������� ��������������� ��������� HTTP-������� 
	  request.setCharacterEncoding("UTF-8");     
	  // ������� �� HTTP-������� �������� 'message' 
	  String message = (String)request.getParameter("message"); 
	  // ���� ��������� �� ������, �� 
	  if (message!=null && !"".equals(message)) { 
		  // �� ����� �� ������ �������� ������ �� ������ ChatUser 
		  ChatUser author = activeUsers.get((String) request.getSession().getAttribute("name"));
		  synchronized (messages) { 
			  // �������� � ������ ��������� ����� 
			  messages.add(new ChatMessage(message, author, Calendar.getInstance().getTimeInMillis())); 
			  } 
		  } 
	  // ������������� ������������ �� �������� � ������ ��������� 
	  response.sendRedirect("/newfolder_war_exploded/compose_message.htm");
	  } 
  } 