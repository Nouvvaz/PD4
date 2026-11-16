package com.feria.controlador;

import com.feria.modelo.Editorial;
import com.feria.persistencia.EditorialDAO;

import java.util.List;

/** Consulta de editoriales registradas. */
public class ControladorEditoriales {
    private final EditorialDAO editorialDAO;

    public ControladorEditoriales(EditorialDAO editorialDAO) {
        this.editorialDAO = editorialDAO;
    }

    public List<Editorial> listarEditoriales() {
        return editorialDAO.listarTodas();
    }
}
