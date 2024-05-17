import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@WebServlet("/time")
public class TimeServlet extends HttpServlet {
    private TemplateEngine templateEngine;

    @Override
    public void init() throws ServletException {
        ServletContextTemplateResolver templateResolver = new ServletContextTemplateResolver(getServletContext());
        templateResolver.setPrefix("/WEB-INF/templates/");
        templateResolver.setSuffix(".html");
        templateResolver.setTemplateMode("HTML");

        templateEngine = new TemplateEngine();
        templateEngine.setTemplateResolver(templateResolver);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/html");

        String timezone = req.getParameter("timezone");
        ZoneId zoneId;

        if (timezone != null) {
            try {
                zoneId = ZoneId.of(timezone);
                Cookie cookie = new Cookie("lastTimezone", timezone);
                cookie.setMaxAge(60 * 60 * 24 * 30); // зберігати протягом 30 днів
                resp.addCookie(cookie);
            } catch (Exception e) {
                zoneId = ZoneId.of("UTC");
            }
        } else {
            Cookie[] cookies = req.getCookies();
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if (cookie.getName().equals("lastTimezone")) {
                        timezone = cookie.getValue();
                        break;
                    }
                }
            }
            if (timezone != null) {
                zoneId = ZoneId.of(timezone);
            } else {
                zoneId = ZoneId.of("UTC");
            }
        }

        LocalDateTime currentTime = LocalDateTime.now(zoneId);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss '[" + zoneId + "]'");
        String formattedTime = currentTime.format(formatter);

        WebContext context = new WebContext(req, resp, getServletContext(), req.getLocale());
        context.setVariable("time", formattedTime);

        templateEngine.process("time", context, resp.getWriter());
    }
}
