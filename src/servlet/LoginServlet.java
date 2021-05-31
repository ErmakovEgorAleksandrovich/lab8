package servlet; 
 
import java.io.IOException; 
import java.io.PrintWriter; 
import java.util.Calendar; 
import javax.servlet.ServletException; 
import javax.servlet.http.Cookie; 
import javax.servlet.http.HttpServletRequest; 
import javax.servlet.http.HttpServletResponse;

import entity.ChatMessage;
import entity.ChatUser;
 
public class LoginServlet extends ChatServlet { 
   
  private static final long serialVersionUID = 1L; 
  //������ ���������� �������������
  private int KolCh = 1; 
 
  // ������������ ������, � �������� 
  private int sessionTimeout = 10*60; 
        
  public void init() throws ServletException { 
    super.init(); 
    // ��������� �� ������������ �������� ��������� SESSION_TIMEOUT 
    String value = getServletConfig().getInitParameter("SESSION_TIMEOUT"); 
    // ���� �� �����, �������������� ������������ ������ �� ��������� 
    if (value!=null) { 
      sessionTimeout = Integer.parseInt(value); 
    } 
  } 
    // ����� ����� ������ ��� ��������� � �������� HTTP-������� GET 
    // �.�. ����� ������������ ������ ��������� ����� � ��������  
  protected void doGet(HttpServletRequest request, HttpServletResponse 
		  response) throws ServletException, IOException { 
    // ���������, ���� �� ��� � ������ �������� ��� ������������? 
    String name = (String)request.getSession().getAttribute("name"); 
    // ������� �� ������ �������� � ���������� ������ (���������) 
    String errorMessage = (String)request.getSession().getAttribute("error"); 
    // ������������� ���������� ������ ���������� ���� 
    String previousSessionId = null;     
    // ���� � ������ ��� �� ���������, �� ����������  
// ������������ ��� ����� cookie 
    if (name==null) { 
    
      // ����� cookie � ������ sessionId 
      for (Cookie aCookie: request.getCookies()) { 
        if (aCookie.getName().equals("sessionId")) { 
          // ��������� �������� ����� cookie �  
        	// ��� ������ ������������� ������ 
          previousSessionId = aCookie.getValue(); 
          break; 
        } 
      } 
      if (previousSessionId!=null) { 
        // �� ����� session cookie 
        // ���������� ����� ������������ � ����� sessionId  
        for (ChatUser aUser: activeUsers.values()) { 
          if (aUser.getSessionId().equals(previousSessionId)) { 
            // �� ����� ������, �.�. ������������ ��� 
            name = aUser.getName(); 
          aUser.setSessionId(request.getSession().getId()); 
          } 
        }         
      }       
    }     
    // ���� � ������ ������� �� ������ ��� ������������, ��... 
    if (name!=null && !"".equals(name)) { 
      errorMessage = processLogonAttempt(name, request, response); 
    }  
    // ������������ ���������� ������ ���. �������� ����� 
    // ������ ��������� HTTP-������ 
    response.setCharacterEncoding("utf8"); 
    // �������� ����� ������ ��� HTTP-������ 
    PrintWriter pw = response.getWriter(); 
    pw.println("<html><head><title>Chat by Diana</title><meta http-" +
    		"equiv='Content-Type' content='text/html; charset=utf-8'/></head>"); 
    // ���� �������� ������ - �������� � ��� 
    if (errorMessage!=null) { 
      pw.println("<p><font color='red'>" + errorMessage + "</font></p>"); 
    }
    // ������� ����� 
    pw.println("<form action='/newfolder_war_exploded/' method='post'>Введите имя:" +
    "<input type='text' name='name' value=''><input type='submit' value='Отправить " + "'>");
    pw.println("</form></body></html>"); 
    // �������� ��������� �� ������ � ������ 
    request.getSession().setAttribute("error", null); 
  } 
 
  // ����� ����� ������ ��� ��������� � �������� HTTP-������� POST 
  // �.�. ����� ������������ ���������� �������� ������ 
  protected void doPost(HttpServletRequest request, HttpServletResponse 
response) throws ServletException, IOException { 
    // ������ ��������� HTTP-������� - ����� �����!  
// ����� ������ �������� ����� �����������     
    request.setCharacterEncoding("UTF-8"); 
    // ������� �� HTTP-������� �������� ��������� 'name' 
    String name = (String)request.getParameter("name"); 
    // ��������, ��� ���������� ������ ��� 
    String errorMessage = null; 
    
    
    if (name==null || "".equals(name)) { 
      // ������ ��� ����������� - �������� �� ������ 
      errorMessage = "Enter name!";
    }
    else if(ChatUser.getKol() >= KolCh)
    	errorMessage = "Enter smth" ;
    else{ 
      // ���� �� �� ������, �� ���������� ���������� ������ 
      errorMessage = processLogonAttempt(name, request, response); 

    } 
    if (errorMessage!=null) { 
      // �������� ��� ������������ � ������       
      request.getSession().setAttribute("name", null); 
      // ��������� � ������ ��������� �� ������ 
      request.getSession().setAttribute("error", errorMessage); 
      // �������������� ������� �� �������� �������� � ������ 
      response.sendRedirect(response.encodeRedirectURL("/newfolder_war_exploded/"));
    } 
  } 
   
  // ���������� ��������� �������� ��������� ������ ��� null 

String processLogonAttempt(String name, HttpServletRequest request, 
HttpServletResponse response) throws IOException { 
    // ���������� ������������� Java-������ ������������ 
    String sessionId = request.getSession().getId(); 
    // ������� �� ������ ������, ��������� � ���� ������ 
    ChatUser aUser = activeUsers.get(name); 
    if (aUser==null) { 
      // ���� ��� ��������, �� ��������  
// ������ ������������ � ������ �������� 
      aUser = new ChatUser(name, Calendar.getInstance().getTimeInMillis(), sessionId); 
      // ��� ��� ������������ ����������� �������  
// �� ��������� ������������� 
      // �� ���������� ������������� �� ������� 
      synchronized (activeUsers) { 
    	  activeUsers.put(aUser.getName(), aUser);    
   
      } 
    }

    //if (aUser.getKol() >= 1)
    if (aUser.getSessionId().equals(sessionId) || 
    		aUser.getLastInteractionTime()<(Calendar.getInstance().getTimeInMillis()-
    				sessionTimeout*1000)) { 
      // ���� ��������� ��� ����������� �������� ������������, 
      // ���� ��� ������������ ����-�� �������, �� ������ �������, 
      // �� �������� ������ ������������ �� ��� ��� 
       
      // �������� ��� ������������ � ������ 
      request.getSession().setAttribute("name", name); 
      // �������� ����� �������������� ������������ � �������� 
   
  aUser.setLastInteractionTime(Calendar.getInstance().getTimeInMillis()); 
      // �������� ������������� ������ ������������ � cookies 
      Cookie sessionIdCookie = new Cookie("sessionId", sessionId); 
      // ���������� ���� �������� cookie 1 ��� 
      sessionIdCookie.setMaxAge(60*60*24*365); 
      // �������� cookie � HTTP-����� 
      response.addCookie(sessionIdCookie);
      messages.add(new ChatMessage("(The user has entered the chat) ",aUser, Calendar.getInstance().getTimeInMillis()));
  response.sendRedirect(response.encodeRedirectURL("/newfolder_war_exploded/view.htm"));
      // ������� null, �.�. ��������� �� ������� ��� 
      return null; 
    } else { 
      // ����������� � ������ ��� ��� ���������� �� ���-�� ������. 
      // ����������, �������� � ��������� ������ ������ ��� 
      return " <strong> Имя - " + name + " уже занято</strong>  " +
      		"!";
    }     
  } 
   
} 