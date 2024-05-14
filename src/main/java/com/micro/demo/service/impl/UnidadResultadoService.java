package com.micro.demo.service.impl;

import com.micro.demo.entities.UnidadResultado;
import com.micro.demo.repository.IUnidadResultadoRepository;
import com.micro.demo.service.IUnidadResultadoService;
import com.micro.demo.service.exceptions.IlegalPaginaException;
import com.micro.demo.service.exceptions.NoDataFoundException;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class UnidadResultadoService implements IUnidadResultadoService {

    private final IUnidadResultadoRepository unidadResultadoRepository;

    public UnidadResultadoService(IUnidadResultadoRepository unidadResultadoRepository) {
        this.unidadResultadoRepository = unidadResultadoRepository;
    }

    /**
     * Obtiene las unidades de resultados mediante la paginacion
     *
     * @param pagina numero de pagina
     * @param elementosXpagina elementos que habran en cada pagina
     * @return Lista de unidades de resultados.
     * @throws IlegalPaginaException - Si el numero de pagina es menor a 1
     * @throws NoDataFoundException - Si no se encuentra datos.
     */
    @Override
    public List<UnidadResultado> getAllUnidadResultados(int pagina, int elementosXpagina) {
        if (pagina < 1) {
            throw new IlegalPaginaException();
        }

        Page<UnidadResultado> paginaUnidad =
                unidadResultadoRepository.findAll(PageRequest.of(pagina -1, elementosXpagina,
                        Sort.by("id").ascending()));

        if (paginaUnidad.isEmpty()) {
            throw new NoDataFoundException();
        }

        return paginaUnidad.getContent();
    }

    /**
     * Guardar unidad de resultado
     *
     * @param unidadResultado - Informacion de una unidad resultado
     * */
    @Override
    public void saveUnidadResultado(UnidadResultado unidadResultado) {
        unidadResultadoRepository.save(unidadResultado);
    }

    /**
     * Actualizar una unidad de resultado
     *
     * @param id - Identificador unico de una unidad resultado a actualizar.
     * @param unidadResultado - Informacion de una unidad de resultado.
     * @throws NoDataFoundException - Se lanza si no se encuentran datos.
     * */
    @Override
    public void updateUnidadResultado(Long id, UnidadResultado unidadResultado) {
        UnidadResultado existingUnidad = unidadResultadoRepository.findById(id)
                .orElseThrow(NoDataFoundException::new);

        existingUnidad.setCorteEvaluacion(unidadResultado.getCorteEvaluacion());
        existingUnidad.setEstatus(unidadResultado.isEstatus());
        existingUnidad.setCriterioDesempeno(unidadResultado.getCriterioDesempeno());
        existingUnidad.setInstrumentoEvaluacion(unidadResultado.getInstrumentoEvaluacion());
        existingUnidad.setTipoEvidencia(unidadResultado.getTipoEvidencia());

        unidadResultadoRepository.save(existingUnidad);
    }

    /**
     * Elimina una unidad de resultado por su identificador único.
     *
     * @param id - Identificador único de una unidad de resultado a eliminar
     * @throws NoDataFoundException - Se lanza si no se encuentran datos.
     */
    @Override
    public void deleteUnidadResultado(Long id) {
        unidadResultadoRepository.findById(id)
                .orElseThrow(NoDataFoundException::new);

        unidadResultadoRepository.deleteById(id);
    }
}
