package servlet;

import java.io.IOException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.DAO;
import model.DataSourceFactory;

/**
 * Le contrôleur de l'application
 * @author rbastide
 */
@WebServlet(name = "discountEditor", urlPatterns = {"/discountEditor"})
public class DiscountEditorController extends HttpServlet {

	/**
	 * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
	 *
	 * @param request servlet request
	 * @param response servlet response
	 * @throws ServletException if a servlet-specific error occurs
	 * @throws IOException if an I/O error occurs
	 */
	protected void processRequest(HttpServletRequest request, HttpServletResponse response)
		throws ServletException, IOException {
		// Quelle action a servi à appeler la servlet ? (Ajouter, Supprimer ou aucune = afficher)
		String action = request.getParameter("action");
		action = (action == null) ? "" : action; // Pour le switch qui n'aime pas les null
		String code = request.getParameter("code");
		String taux = request.getParameter("taux");
		try {
			DAO dao = new DAO(DataSourceFactory.getDataSource());
			request.setAttribute("codes", dao.allCodes());			
			switch (action) {
				case "ADD": // Requête d'ajout (vient du formulaire de saisie)
					dao.addDiscountCode(code, Float.valueOf(taux));
					request.setAttribute("message", "Code " + code + " Ajouté");
					request.setAttribute("codes", dao.allCodes());								
					break;
				case "DELETE": // Requête de suppression (vient du lien hypertexte)
					try {
						dao.deleteDiscountCode(code);
						request.setAttribute("message", "Code " + code + " Supprimé");
						request.setAttribute("codes", dao.allCodes());								
					} catch (SQLIntegrityConstraintViolationException e) {
						request.setAttribute("message", "Impossible de supprimer " + code + ", ce code est utilisé.");
					}
					break;
			}
		} catch (Exception ex) {
			Logger.getLogger("discountEditor").log(Level.SEVERE, "Action en erreur", ex);
			request.setAttribute("message", ex.getMessage());
		} finally {

		}
		// On continue vers la page JSP sélectionnée
		request.getRequestDispatcher("ajoutDiscount.jsp").forward(request, response);
	}

	// <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
	/**
	 * Handles the HTTP <code>GET</code> method.
	 *
	 * @param request servlet request
	 * @param response servlet response
	 * @throws ServletException if a servlet-specific error occurs
	 * @throws IOException if an I/O error occurs
	 */
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
		throws ServletException, IOException {
		processRequest(request, response);
	}

	/**
	 * Handles the HTTP <code>POST</code> method.
	 *
	 * @param request servlet request
	 * @param response servlet response
	 * @throws ServletException if a servlet-specific error occurs
	 * @throws IOException if an I/O error occurs
	 */
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
		throws ServletException, IOException {
		processRequest(request, response);
	}

	/**
	 * Returns a short description of the servlet.
	 *
	 * @return a String containing servlet description
	 */
	@Override
	public String getServletInfo() {
		return "Short description";
	}// </editor-fold>

}
