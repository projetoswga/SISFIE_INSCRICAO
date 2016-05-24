package br.com.sisfie.bean;

import java.io.File;
import java.io.FileOutputStream;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.model.SelectItem;

import org.hibernate.Hibernate;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.TabChangeEvent;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

import br.com.arquitetura.bean.PaginableBean;
import br.com.arquitetura.excecao.ExcecaoUtil;
import br.com.arquitetura.util.DateUtil;
import br.com.arquitetura.util.FacesMessagesUtil;
import br.com.sisfie.dataModel.AreaAtuacaoDataModel;
import br.com.sisfie.dataModel.CargoDataModel;
import br.com.sisfie.dataModel.EsferaGovernoDataModel;
import br.com.sisfie.entidade.AreaConhecimentoCurso;
import br.com.sisfie.entidade.Atuacao;
import br.com.sisfie.entidade.AtuacaoCandidato;
import br.com.sisfie.entidade.CampoPreenchimento;
import br.com.sisfie.entidade.Candidato;
import br.com.sisfie.entidade.CandidatoCargo;
import br.com.sisfie.entidade.CandidatoComplemento;
import br.com.sisfie.entidade.CandidatoPreenchimento;
import br.com.sisfie.entidade.Cargo;
import br.com.sisfie.entidade.Curso;
import br.com.sisfie.entidade.EmailCursoPrivado;
import br.com.sisfie.entidade.EsferaCurso;
import br.com.sisfie.entidade.EsferaGoverno;
import br.com.sisfie.entidade.GradeOficina;
import br.com.sisfie.entidade.Homologacao;
import br.com.sisfie.entidade.HomologacaoCurso;
import br.com.sisfie.entidade.Horario;
import br.com.sisfie.entidade.InscricaoComprovante;
import br.com.sisfie.entidade.InscricaoCurso;
import br.com.sisfie.entidade.InscricaoDocumento;
import br.com.sisfie.entidade.InscricaoGrade;
import br.com.sisfie.entidade.InscricaoInfoComplementar;
import br.com.sisfie.entidade.Localizacao;
import br.com.sisfie.entidade.Municipio;
import br.com.sisfie.entidade.MunicipioCurso;
import br.com.sisfie.entidade.OpcaoListaCandidato;
import br.com.sisfie.entidade.Orgao;
import br.com.sisfie.entidade.OrgaoCurso;
import br.com.sisfie.entidade.Situacao;
import br.com.sisfie.entidade.Status;
import br.com.sisfie.entidade.StatusCurso;
import br.com.sisfie.entidade.StatusInscricao;
import br.com.sisfie.entidade.Turno;
import br.com.sisfie.entidade.Uf;
import br.com.sisfie.entidade.UfCurso;
import br.com.sisfie.entidade.Usuario;
import br.com.sisfie.service.CandidatoService;
import br.com.sisfie.service.CursoService;
import br.com.sisfie.service.DistribuirService;
import br.com.sisfie.service.EsferaCursoService;
import br.com.sisfie.service.HomologacaoCursoService;
import br.com.sisfie.service.HorarioService;
import br.com.sisfie.service.InscricaoCursoService;
import br.com.sisfie.service.OficinaService;
import br.com.sisfie.service.OrgaoService;
import br.com.sisfie.service.PacoteService;
import br.com.sisfie.service.SelecaoOficinaService;
import br.com.sisfie.service.SelecaoPacoteService;
import br.com.sisfie.service.TurmaService;
import br.com.sisfie.util.CnpjUtil;
import br.com.sisfie.util.Constantes;
import br.com.sisfie.util.CpfUtil;
import br.com.sisfie.util.ImagemUtil;
import br.com.sisfie.util.TipoEmail;

@ManagedBean(name = "inscricaoCursoBean")
@ViewScoped
public class InscricaoCursoBean extends PaginableBean<InscricaoCurso> {

	private static final long serialVersionUID = 1L;

	@ManagedProperty(value = "#{inscricaoCursoService}")
	protected InscricaoCursoService inscricaoCursoService;

	@ManagedProperty(value = "#{cursoService}")
	protected CursoService cursoService;

	@ManagedProperty(value = "#{OrgaoService}")
	protected OrgaoService orgaoService;

	@ManagedProperty(value = "#{esferaCursoService}")
	protected EsferaCursoService esferaCursoService;

	@ManagedProperty(value = "#{login}")
	private LoginBean loginBean;

	@ManagedProperty(value = "#{candidatoBean}")
	private CandidatoBean candidatoBean;

	@ManagedProperty(value = "#{homologacaoCursoService}")
	protected HomologacaoCursoService homologacaoCursoService;

	@ManagedProperty(value = "#{oficinaService}")
	protected OficinaService oficinaService;

	@ManagedProperty(value = "#{selecaoOficinaService}")
	protected SelecaoOficinaService selecaoOficinaService;

	@ManagedProperty(value = "#{selecaoPacoteService}")
	protected SelecaoPacoteService selecaoPacoteService;

	@ManagedProperty(value = "#{pacoteService}")
	protected PacoteService pacoteService;

	@ManagedProperty(value = "#{distribuirService}")
	protected DistribuirService distribuirService;

	@ManagedProperty(value = "#{horarioService}")
	protected HorarioService horarioService;

	@ManagedProperty(value = "#{turmaService}")
	protected TurmaService turmaService;

	@ManagedProperty(value = "#{candidatoService}")
	protected CandidatoService candidatoService;

	public static final Integer PERSISTIR = 1;
	public static final Integer REMOVER = 2;
	private static final Integer LIMITE_INSCRICAO_FORA_PRAZO = 17;
	private static final Integer PRAZO_LIMITE_CANCELAMENTO = 19;

	private String context = getContextPath();
	private Orgao orgaoSelecionado;
	private List<Uf> UfOrgaos;
	private Integer idUfOrgaoSelecionado;
	private List<Municipio> municipioOrgaos;
	private Integer idMunicipioSelecionado;
	private List<SelectItem> cursos;
	private Integer idCursoSelecionado;
	private List<EsferaGoverno> listaEsferaGovernos;
	private List<InscricaoCurso> inscricoes;
	private Boolean flgNacionalidade;
	private List<Municipio> municipioEnderecos;
	private Integer idMunicipioEnderecoSelecionado;
	private Integer idUfEnderecoSelecionado;
	private List<Uf> UfEnderecos;
	private InscricaoCurso inscricaoCursoSelecionado;
	private List<String> idsAreaAtuacao;
	private AreaAtuacaoDataModel areaAtuacaoDataModel;
	private Atuacao[] areaAtuacoes;
	private List<Atuacao> areaAtuacoesAdicionados;
	private List<AtuacaoCandidato> atuacaoCandidatos;
	private List<AtuacaoCandidato> atuacaoCandidatosExclusao;
	private Atuacao areaAtuacaoExclusao;
	private CargoDataModel cargoDataModel;
	private Cargo[] cargos;
	private List<Cargo> cargosAdicionados;
	private List<CandidatoCargo> candidatoCargos;
	private List<CandidatoCargo> candidatoCargosExclusao;
	private Cargo cargoExclusao;
	private EsferaGoverno[] esferaGovernos;
	private EsferaGovernoDataModel esferaGovernoDataModel;
	private List<Curso> outrosCursos;
	private String confirmacaoEmail;
	private boolean alterarEmail;
	private int abaId = 0;
	private boolean mostrarDadosBancario;
	private boolean mostrarNivelEnsino;
	private boolean mostrarExperienciaProfissional;
	private boolean mostrarEmailResponsavel;
	private boolean mostrarBotaoConfirmar;
	private CandidatoComplemento candidatoComplemento;
	private String emailResponsavel;
	private String emailResponsavelConfirmacao;
	private List<UfCurso> ufCursos;
	private List<MunicipioCurso> municipioCursos;
	private List<EsferaGoverno> esferasGoverno;
	private List<InscricaoComprovante> comprovantes;
	private List<InscricaoDocumento> documentos;
	private InscricaoComprovante comprovanteExcluir;
	private InscricaoDocumento documentoExcluir;
	private boolean mostrarInformacaoPagamento;
	private InscricaoInfoComplementar inscricaoInfoComplementar;
	private List<AreaConhecimentoCurso> areaConhecimentoCursos;
	private boolean mostrarSelecaoOficina;
	private boolean mostrarSelecaoPacote;
	private Curso cursoSelecionado;
	private Integer quantidadeInscrito;
	private Integer idHorario;
	private List<Horario> horarios;
	private Integer idGradeOficina;
	private List<GradeOficina> listaGradeOficinas;
	private List<GradeOficina> gradeOficinas;
	private String textoConfirmacaoSelecaoOficina;
	private GradeOficina gradeOficinaExclusao;
	private List<InscricaoGrade> listaInscricaoGrade;
	private GradeOficina gradeOficina;
	private List<GradeOficina> listaGradeOficinasVinculadas;
	private Integer totalCargaHoraria = 0;
	private boolean isParceiro = false;
	private boolean mostrarBotaoCancelar;
	private boolean isInstrutor;
	private Map<Curso, Boolean> mapaInstrutorCurso;

	public InscricaoCursoBean() {
		inscricaoInfoComplementar = new InscricaoInfoComplementar();
		comprovantes = new ArrayList<InscricaoComprovante>();
		documentos = new ArrayList<InscricaoDocumento>();
		esferasGoverno = new ArrayList<EsferaGoverno>();
		ufCursos = new ArrayList<UfCurso>();
		municipioCursos = new ArrayList<MunicipioCurso>();
		candidatoComplemento = new CandidatoComplemento();
		UfOrgaos = new ArrayList<Uf>();
		municipioOrgaos = new ArrayList<Municipio>();
		cursos = new ArrayList<SelectItem>();
		listaEsferaGovernos = new ArrayList<EsferaGoverno>();
		inscricoes = new ArrayList<InscricaoCurso>();
		municipioEnderecos = new ArrayList<Municipio>();
		UfEnderecos = new ArrayList<Uf>();
		inscricaoCursoSelecionado = new InscricaoCurso();
		areaAtuacoesAdicionados = new ArrayList<Atuacao>();
		atuacaoCandidatos = new ArrayList<AtuacaoCandidato>();
		atuacaoCandidatosExclusao = new ArrayList<AtuacaoCandidato>();
		areaAtuacaoExclusao = new Atuacao();
		cargoExclusao = new Cargo();
		cargosAdicionados = new ArrayList<Cargo>();
		candidatoCargos = new ArrayList<CandidatoCargo>();
		candidatoCargosExclusao = new ArrayList<CandidatoCargo>();
		areaConhecimentoCursos = new ArrayList<AreaConhecimentoCurso>();
		cursoSelecionado = new Curso();
		horarios = new ArrayList<Horario>();
		listaGradeOficinas = new ArrayList<GradeOficina>();
		gradeOficinas = new ArrayList<GradeOficina>();
		gradeOficinaExclusao = new GradeOficina();
		listaInscricaoGrade = new ArrayList<InscricaoGrade>();
		gradeOficina = new GradeOficina();
		listaGradeOficinasVinculadas = new ArrayList<GradeOficina>();
		getModel().setCandidato(new Candidato());
		getModel().setCurso(new Curso());
		getModel().getCurso().setLocalizacao(new Localizacao());
	}

	public void carregarOficinasGradeOficina(AjaxBehaviorEvent event) {
		try {
			List<Integer> listaOficinasDisponiveis = oficinaService.recuperarIdsOficinasDisponiveis(idCursoSelecionado);

			List<Integer> idsOficinasSelecionadas = new ArrayList<Integer>();
			for (GradeOficina grade : listaGradeOficinas) {
				idsOficinasSelecionadas.add(grade.getOficina().getId());
			}

			gradeOficinas = oficinaService.recuperarGradeOficinaPorHorario(listaOficinasDisponiveis, idsOficinasSelecionadas,
					idHorario);

			/**
			 * Removendo da gradeOficinas as grades de oficinas vinculadas, ou seja, remove as oficinas que tem complemento em outro horário
			 * para não conflitar com as que já estão selecionadas. (gradeOficicas ---> Oficinas disponíveis para seleção);
			 * (listaGradeOficinasVinculadas ---> Continuação das oficinas selecionadas em outro horário); (listaGradeOficinas ---> Oficinas
			 * já adicionadas); (gradeExclusao ---> Oficinas à serem removidas por conflitarem com outras oficinas já selecionadas).
			 */
			listaGradeOficinasVinculadas = new ArrayList<GradeOficina>();
			List<GradeOficina> gradeExclusao = new ArrayList<GradeOficina>();
			for (GradeOficina gradeDisponivel : gradeOficinas) {
				if (gradeDisponivel.getOficina().getNumCargaHoraria() > 4) {
					listaGradeOficinasVinculadas = oficinaService.recuperarGradeOficinasVinculadas(
							gradeDisponivel.getHorario().getId(), gradeDisponivel.getOficina().getId(),
							gradeDisponivel.getTurma().getId());
					if (!listaGradeOficinasVinculadas.isEmpty()) {
						for (GradeOficina gradeVinculada : listaGradeOficinasVinculadas) {
							for (GradeOficina gradeAdicionada : listaGradeOficinas) {
								if (gradeAdicionada.getHorario().getId().equals(gradeVinculada.getHorario().getId())) {
									gradeExclusao.add(gradeDisponivel);
								}
							}
						}
					}
				}
			}

			if (!gradeExclusao.isEmpty()) {
				gradeOficinas.removeAll(gradeExclusao);
			}

			if (gradeOficinas != null && !gradeOficinas.isEmpty()) {
				Collections.sort(gradeOficinas, new Comparator<GradeOficina>() {
					@Override
					public int compare(GradeOficina o1, GradeOficina o2) {
						return o1.getOficina().getNomOficina().trim().compareTo(o2.getOficina().getNomOficina().trim());
					}
				});
			}
		} catch (Exception e) {
			ExcecaoUtil.tratarExcecao(e);
		}
	}

	@SuppressWarnings("unchecked")
	public void adicionarOficina() {
		try {
			if (idHorario == null || idHorario == 0) {
				FacesMessagesUtil.addErrorMessage("Horário", "Campo obrigatório!");
				return;
			}
			if (idGradeOficina == null || idGradeOficina == 0) {
				FacesMessagesUtil.addErrorMessage("Oficina", "Campo obrigatório!");
				return;
			}

			gradeOficina = (GradeOficina) universalManager.get(GradeOficina.class, idGradeOficina);

			Integer percentualParceiro = 0;
			if (cursoSelecionado.getNumPercentualVagasParceiro() > 0) {
				percentualParceiro = (int) Math.floor(
						cursoSelecionado.getNumPercentualVagasParceiro() * gradeOficina.getSala().getNumCapacidade() / 100);
			}

			isParceiro = false;

			// Verificando se o candidato é parceiro
			EmailCursoPrivado emailParceiroConsulta = new EmailCursoPrivado();
			emailParceiroConsulta.setEmail(loginBean.getModel().getEmailInstitucional());
			emailParceiroConsulta.setCurso(new Curso(cursoSelecionado.getId()));
			List<EmailCursoPrivado> listaEmailCunsulta = universalManager.listBy(emailParceiroConsulta);
			if (listaEmailCunsulta != null && !listaEmailCunsulta.isEmpty()) {
				isParceiro = true;
			}

			// Recuperando todos os parceiros do curso
			EmailCursoPrivado email = new EmailCursoPrivado();
			email.setCurso(new Curso(cursoSelecionado.getId()));
			List<EmailCursoPrivado> emailsParceiros = universalManager.listBy(email);

			List<Integer> idsParceiros = new ArrayList<Integer>();
			for (EmailCursoPrivado emailParceiro : emailsParceiros) {
				Integer idParceiro = candidatoService.recuperarParceiro(emailParceiro.getEmail());
				if (idParceiro != null && idParceiro != 0) {
					idsParceiros.add(idParceiro);
				}
			}

			if (!isParceiro) {
				Long countInscritos = oficinaService.countInscritosPorGradeOficina(idCursoSelecionado, gradeOficina.getId(),
						idsParceiros);

				if (countInscritos >= (gradeOficina.getSala().getNumCapacidade() - percentualParceiro)) {
					FacesMessagesUtil.addErrorMessage("Inscrições Encerradas!", "Para a oficina selecionada não há mais vagas.");
					return;
				}
			} else {
				Long countParceirosInscritos = oficinaService.countParceirosInscritos(idCursoSelecionado, gradeOficina.getId(),
						idsParceiros);

				if (countParceirosInscritos >= percentualParceiro) {
					FacesMessagesUtil.addErrorMessage("Inscrições Encerradas!", "Para a oficina selecionada não há mais vagas.");
					return;
				}
			}

			boolean exite = false;
			for (GradeOficina grade : listaGradeOficinas) {
				if (grade.getId().equals(gradeOficina.getId())) {
					exite = true;
					break;
				}
			}

			if (!exite) {
				/**
				 * Caso a grade seja maior do que 4 horas, significa que a mesma oficina será dada em outro horário. É necessário adicionar
				 * essas oficinas tbm, com isso será mostrado um popup para exigir e pedir a confirmação do usuário, do contrário salva sem
				 * essa confirmação.
				 */
				if (gradeOficina.getOficina().getNumCargaHoraria() > 4) {
					RequestContext context = RequestContext.getCurrentInstance();
					context.execute("confirmarSelecaoOficina.show();");
				} else {
					confirmarSelecaoOficina();
				}
			} else {
				FacesMessagesUtil.addErrorMessage("", "Oficina já adicionada!");
				return;
			}
		} catch (Exception e) {
			ExcecaoUtil.tratarExcecao(e);
		}
	}

	public void confirmarSelecaoOficina() {
		listaGradeOficinasVinculadas = oficinaService.recuperarGradeOficinasVinculadas(idHorario,
				gradeOficina.getOficina().getId(), gradeOficina.getTurma().getId());

		listaGradeOficinas.add(gradeOficina);

		for (GradeOficina gradeOficinaVinculada : listaGradeOficinasVinculadas) {
			listaGradeOficinas.add(gradeOficinaVinculada);
			horarios.remove(gradeOficinaVinculada.getHorario());
		}

		if (listaGradeOficinas != null && !listaGradeOficinas.isEmpty()) {
			Collections.sort(listaGradeOficinas, new Comparator<GradeOficina>() {
				@Override
				public int compare(GradeOficina o1, GradeOficina o2) {
					return o1.getHorario().getDatHoraInicio().compareTo(o2.getHorario().getDatHoraInicio());
				}
			});
		}

		horarios.remove(gradeOficina.getHorario());
		limparSelecao();
		totalCargaHoraria += gradeOficina.getOficina().getNumCargaHoraria();
		FacesMessagesUtil.addInfoMessage("", "Oficina Adiciona com sucesso!");
	}

	public void deleteOficina(GradeOficina gradeOficina) {

		gradeOficinaExclusao = gradeOficina;

		if (gradeOficina.getOficina().getNumCargaHoraria() > 4) {
			RequestContext context = RequestContext.getCurrentInstance();
			context.execute("confirmarExclusaoOficina.show();");
		} else {
			confirmarExclusaoGradeOficina();
		}
	}

	public void confirmarExclusaoGradeOficina() {

		listaGradeOficinas.remove(gradeOficinaExclusao);

		if (gradeOficinaExclusao.getOficina().getNumCargaHoraria() > 4) {
			List<GradeOficina> gradeOficinasVinculadas = oficinaService.recuperarGradeOficinasVinculadas(
					gradeOficinaExclusao.getHorario().getId(), gradeOficinaExclusao.getOficina().getId(),
					gradeOficinaExclusao.getTurma().getId());
			if (gradeOficinasVinculadas != null && !gradeOficinasVinculadas.isEmpty()) {
				for (GradeOficina gradeOficinaVinculada : gradeOficinasVinculadas) {
					listaGradeOficinas.remove(gradeOficinaVinculada);
					horarios.add(gradeOficinaVinculada.getHorario());
				}
			}
		}

		horarios.add(gradeOficinaExclusao.getHorario());
		if (horarios != null && !horarios.isEmpty()) {
			Collections.sort(horarios, new Comparator<Horario>() {
				@Override
				public int compare(Horario o1, Horario o2) {
					return o1.getDatHoraInicio().compareTo(o2.getDatHoraInicio());
				}
			});
		}

		if (listaGradeOficinas != null && !listaGradeOficinas.isEmpty()) {
			Collections.sort(listaGradeOficinas, new Comparator<GradeOficina>() {
				@Override
				public int compare(GradeOficina o1, GradeOficina o2) {
					return o1.getHorario().getDatHoraInicio().compareTo(o2.getHorario().getDatHoraInicio());
				}
			});
		}
		totalCargaHoraria -= gradeOficinaExclusao.getOficina().getNumCargaHoraria();
		limparSelecao();
	}

	private void limparSelecao() {
		idHorario = 0;
		idGradeOficina = 0;
		gradeOficinas.clear();
		gradeOficinaExclusao = new GradeOficina();
	}

	private void verificarBotaoConfirmar() {
		mostrarBotaoConfirmar = false;
		if (inscricaoCursoSelecionado.getStatusUltimo().getId().equals(Status.HOMOLOGADO)) {
			mostrarBotaoConfirmar = true;
		}
	}

	public void carregaMsg() {
		Boolean save = (Boolean) getSessionMap().remove("saveIscricao");
		if (save != null && save) {
			abaId = 1;
			FacesMessagesUtil.addInfoMessage("Inscrição ", "Realizada com sucesso!");
		}
		Boolean saveDados = (Boolean) getSessionMap().remove("saveDados");
		if (saveDados != null && saveDados) {
			abaId = 0;
			FacesMessagesUtil.addInfoMessage("Dados Iniciais ", "salvo com sucesso!");
		}
		Boolean confirmarComprovante = (Boolean) getSessionMap().remove("confirmarComprovante");
		if (confirmarComprovante != null && confirmarComprovante) {
			abaId = 2;
			FacesMessagesUtil.addInfoMessage("Comprovante(s) ", " Salvo com sucesso!");
		}
	}

	@SuppressWarnings("unchecked")
	@PostConstruct
	public void carregarTela() {
		try {
			if (loginBean != null && loginBean.getModel() != null && loginBean.getModel().getId() != null) {

				getModel().setCandidato((Candidato) universalManager.get(Candidato.class, loginBean.getModel().getId()));
				Hibernate.initialize(getModel().getCandidato().getMunicipioOrgao());
				Hibernate.initialize(getModel().getCandidato().getMunicipioOrgao().getUf());
				Hibernate.initialize(getModel().getCandidato().getOrgao());
				Hibernate.initialize(getModel().getCandidato().getMunicipioEndereco());
				idMunicipioSelecionado = getModel().getCandidato().getMunicipioOrgao().getId();
				idMunicipioEnderecoSelecionado = getModel().getCandidato().getMunicipioEndereco().getId();
				idUfOrgaoSelecionado = getModel().getCandidato().getMunicipioOrgao().getUf().getId();
				idUfEnderecoSelecionado = getModel().getCandidato().getMunicipioEndereco().getUf().getId();
				orgaoSelecionado = getModel().getCandidato().getOrgao();
			}

			if (UfOrgaos.isEmpty()) {
				UfOrgaos = universalManager.getAll(Uf.class);
				Collections.sort(UfOrgaos, new Comparator<Uf>() {
					@Override
					public int compare(Uf o1, Uf o2) {
						return o1.getSigla().trim().compareTo(o2.getSigla());
					}
				});
			}

			if (UfEnderecos.isEmpty()) {
				UfEnderecos = universalManager.getAll(Uf.class);
				Collections.sort(UfEnderecos, new Comparator<Uf>() {
					@Override
					public int compare(Uf o1, Uf o2) {
						return o1.getSigla().trim().compareTo(o2.getSigla());
					}
				});
			}

			if (cursos.isEmpty()) {
				try {
					List<Curso> cursosPesq = cursoService.listarCursosDisponiveis();

					for (Curso item : cursosPesq) {
						
						/**
						 * Verifica se o curso é privado e o candidato tem acesso
						 */
						if (item.getPrivado()) {
							if (!temAcessoCursoPrivado(item)) {
								continue;
							}
						}
						
						/**
						 * Verifica se o candidato é instrutor.
						 */
						item.setInstrutor(false);
						if (!item.getEmailsCursoPrivado().isEmpty() && !item.getFlgPossuiOficina()) {
							mapearInstrutorCurso(item);
						}

						/**
						 * Se não for instrutor, verifica se o curso é por órgão. Caso seja, o participante deve ser de algum órgao que
						 * esteja participando do evento para se inscriver.
						 */
						if (!item.isInstrutor() && item.getOrgaoCursos() != null && !item.getOrgaoCursos().isEmpty()) {
							boolean pertence = false;
							Candidato candidato = (Candidato) universalManager.get(Candidato.class, loginBean.getModel().getId());
							for (OrgaoCurso orgaoCurso : item.getOrgaoCursos()) {
								if (orgaoCurso.getOrgao().getId().equals(candidato.getOrgao().getId())) {
									pertence = true;
									break;
								}
							}
							if (!pertence) {
								continue;
							}
						}

						/**
						 * Verifiva se o candidato é parceiro, caso ele não seja, é necessário verificar se ele é da mesma região do curso
						 */
						if (item.getFlgPossuiOficina()) {
							EmailCursoPrivado emailParceiroConsulta = new EmailCursoPrivado();
							emailParceiroConsulta.setEmail(loginBean.getModel().getEmailInstitucional());
							emailParceiroConsulta.setCurso(new Curso(item.getId()));
							List<EmailCursoPrivado> listaEmailCunsulta = universalManager.listBy(emailParceiroConsulta);
							if (listaEmailCunsulta == null || listaEmailCunsulta.isEmpty()) {
								if (!candidatoMesmaRegiaoCurso(item, loginBean.getModel())) {
									continue;
								}
							}
						}

						String periodo = DateUtil.getDataHora(item.getDtRealizacaoInicio(), "dd/MM/yyyy") + " a "
								+ DateUtil.getDataHora(item.getDtRealizacaoFim(), "dd/MM/yyyy");
						String label = item.getTitulo() + " - " + periodo;
						SelectItem selectItem = new SelectItem(item.getId(), label);
						cursos.add(selectItem);
					}

					/**
					 * Lista de cursos para inscrições de parceiros fora do periodo de inscrição. Requisito do anexo C. O periodo dessa
					 * 'inscrição' compreende da data fim convencional de inscrição até 17 dias antes do início do curso. TODO: Rever isso
					 * com a esaf.
					 */
					List<EmailCursoPrivado> listaParceirosCursosComOficinasForaPrazoInscricao = cursoService
							.listarCursosDisponiveisParceiros(loginBean.getModel().getEmailInstitucional());

					if (listaParceirosCursosComOficinasForaPrazoInscricao != null
							&& !listaParceirosCursosComOficinasForaPrazoInscricao.isEmpty()) {

						for (EmailCursoPrivado emailParceiro : listaParceirosCursosComOficinasForaPrazoInscricao) {
							if (emailParceiro.getCurso().getFlgPossuiOficina() && DateUtil.diferencaEntreDatas(new Date(),
									emailParceiro.getCurso().getDtRealizacaoInicio()) >= LIMITE_INSCRICAO_FORA_PRAZO) {

								String periodo = DateUtil.getDataHora(emailParceiro.getCurso().getDtRealizacaoInicio(),
										"dd/MM/yyyy") + " a "
										+ DateUtil.getDataHora(emailParceiro.getCurso().getDtRealizacaoFim(), "dd/MM/yyyy");
								String label = emailParceiro.getCurso().getTitulo() + " - " + periodo;
								SelectItem selectItem = new SelectItem(emailParceiro.getCurso().getId(), label);
								cursos.add(selectItem);
							}
						}
					}
				} catch (Exception e) {
					ExcecaoUtil.tratarExcecao(e);
				}
				Collections.sort(cursos, new Comparator<SelectItem>() {
					@Override
					public int compare(SelectItem o1, SelectItem o2) {
						return o1.getLabel().trim().compareTo(o2.getLabel().trim());
					}
				});
			}

			if (idCursoSelecionado == null) {
				idCursoSelecionado = 0;
			} else {
				getModel().setCurso((Curso) universalManager.get(Curso.class, idCursoSelecionado));
				try {
					List<EsferaCurso> listaEsferaCursos = esferaCursoService.listarEsferaCursos(idCursoSelecionado);
					for (EsferaCurso esferaCurso : listaEsferaCursos) {
						listaEsferaGovernos.add(esferaCurso.getEsferaGoverno());
					}
				} catch (Exception e) {
					ExcecaoUtil.tratarExcecao(e);
				}
			}

			if (idUfOrgaoSelecionado != null) {
				carregarMunicipios(idUfOrgaoSelecionado, Boolean.FALSE);
			}

			if (idUfEnderecoSelecionado != null) {
				carregarMunicipios(idUfEnderecoSelecionado, Boolean.TRUE);
			}

			List<Integer> idsAtuacoes = null;
			if (getModel().getCandidato().getAtuacaoCandidatos() != null
					&& !getModel().getCandidato().getAtuacaoCandidatos().isEmpty()) {
				idsAtuacoes = new ArrayList<Integer>();
				for (AtuacaoCandidato atuacaoCandidato : getModel().getCandidato().getAtuacaoCandidatos()) {
					areaAtuacoesAdicionados.add(atuacaoCandidato.getAtuacao());
					idsAtuacoes.add(atuacaoCandidato.getAtuacao().getId());
				}
			}
			listarAreaAtuacoes(idsAtuacoes);

			List<Integer> idsCargos = null;
			if (getModel().getCandidato().getCandidatoCargo() != null
					&& !getModel().getCandidato().getCandidatoCargo().isEmpty()) {
				idsCargos = new ArrayList<Integer>();
				for (CandidatoCargo candidatoCargo : getModel().getCandidato().getCandidatoCargo()) {
					cargosAdicionados.add(candidatoCargo.getCargo());
					idsCargos.add(candidatoCargo.getCargo().getId());
				}
			}
			listarCargos(idsCargos);

		} catch (Exception e) {
			ExcecaoUtil.tratarExcecao(e);
		}
	}

	public boolean temAcessoCursoPrivado(Curso curso) throws Exception {
		boolean temAcesso = false;
		for (EmailCursoPrivado email : curso.getEmailsCursoPrivado()) {
			if (email.getEmail().trim().toLowerCase().equals(loginBean.getModel().getEmailInstitucional().trim().toLowerCase())) {
				temAcesso = true;
				break;
			}
		}
		return temAcesso;
	}

	public void mapearInstrutorCurso(Curso curso) throws Exception {
		if (mapaInstrutorCurso == null) {
			mapaInstrutorCurso = new HashMap<>();
		}
		for (EmailCursoPrivado email : curso.getEmailsCursoPrivado()) {
			if (email.getEmail().trim().toLowerCase().equals(loginBean.getModel().getEmailInstitucional().trim().toLowerCase())) {
				if (email.getTipo() != null && email.getTipo().equals(TipoEmail.INSTRUTOR.getTipo())) {
					curso.setInstrutor(true);
					mapaInstrutorCurso.put(curso, curso.isInstrutor());
				}
				return;
			}
		}
	}

	public void onChange(TabChangeEvent event) {
		idCursoSelecionado = null;
		listaEsferaGovernos = new ArrayList<EsferaGoverno>();
		esferaGovernos = null;
		mostrarDadosBancario = false;
		mostrarNivelEnsino = false;
		mostrarExperienciaProfissional = false;
		mostrarEmailResponsavel = false;
		mostrarSelecaoOficina = false;
		mostrarSelecaoPacote = false;
		candidatoComplemento = new CandidatoComplemento();
		emailResponsavel = null;
		emailResponsavelConfirmacao = null;
		municipioCursos = new ArrayList<MunicipioCurso>();
		ufCursos = new ArrayList<UfCurso>();
		inscricaoCursoSelecionado = new InscricaoCurso();
		cursoSelecionado = new Curso();
	}

	private void listarAreaAtuacoes(List<Integer> idsAtuacoes) {
		List<Atuacao> listaAtuacao = new ArrayList<Atuacao>();
		try {
			listaAtuacao = inscricaoCursoService.listarAreaAtuacoes(idsAtuacoes);
		} catch (Exception e) {
			ExcecaoUtil.tratarExcecao(e);
		}
		Collections.sort(listaAtuacao, new Comparator<Atuacao>() {
			@Override
			public int compare(Atuacao o1, Atuacao o2) {
				return o1.getDescricao().trim().compareTo(o2.getDescricao());
			}
		});
		areaAtuacaoDataModel = new AreaAtuacaoDataModel(listaAtuacao);
	}

	private void listarCargos(List<Integer> idsCargos) {
		List<Cargo> listaCargo = new ArrayList<Cargo>();
		try {
			listaCargo = inscricaoCursoService.listarCargos(idsCargos);
		} catch (Exception e) {
			ExcecaoUtil.tratarExcecao(e);
		}
		Collections.sort(listaCargo, new Comparator<Cargo>() {
			@Override
			public int compare(Cargo o1, Cargo o2) {
				return o1.getDescricao().trim().compareTo(o2.getDescricao());
			}
		});
		cargoDataModel = new CargoDataModel(listaCargo);
	}

	private void listarEsferaGovenos() {
		listaEsferaGovernos = new ArrayList<EsferaGoverno>();
		getModel().setCurso((Curso) universalManager.get(Curso.class, idCursoSelecionado));
		try {
			List<EsferaCurso> listaEsferaCursos = esferaCursoService.listarEsferaCursos(idCursoSelecionado);
			for (EsferaCurso esferaCurso : listaEsferaCursos) {
				listaEsferaGovernos.add(esferaCurso.getEsferaGoverno());
			}
		} catch (Exception e) {
			ExcecaoUtil.tratarExcecao(e);
		}
		Collections.sort(listaEsferaGovernos, new Comparator<EsferaGoverno>() {
			@Override
			public int compare(EsferaGoverno o1, EsferaGoverno o2) {
				return o1.getDescricao().trim().compareTo(o2.getDescricao());
			}
		});
		esferaGovernoDataModel = new EsferaGovernoDataModel(listaEsferaGovernos);
	}

	public void adicionarAtuacoes() {
		if (areaAtuacoes != null && areaAtuacoes.length > 0) {
			List<Integer> idsAtuacoes = new ArrayList<Integer>();
			principal: for (int i = 0; i < areaAtuacoes.length; i++) {
				areaAtuacoesAdicionados.add(areaAtuacoes[i]);
				// Caso tenha registro na lista mas não na base
				boolean existe = false;
				if (atuacaoCandidatos != null && !atuacaoCandidatos.isEmpty()) {
					for (AtuacaoCandidato atuacaoCandidato : atuacaoCandidatos) {
						if (atuacaoCandidato.getAtuacao().getId().equals(areaAtuacoes[i].getId())) {
							existe = true;
						}
					}
					if (!existe) {
						AtuacaoCandidato atuacaoCand = new AtuacaoCandidato();
						atuacaoCand.setAtuacao(areaAtuacoes[i]);
						atuacaoCand.setCandidato(getModel().getCandidato());
						atuacaoCandidatos.add(atuacaoCand);
						continue principal;
					}
				}
				// Primeiro registro
				if (!existe) {
					AtuacaoCandidato atuacaoCand = new AtuacaoCandidato();
					atuacaoCand.setAtuacao(areaAtuacoes[i]);
					atuacaoCand.setCandidato(getModel().getCandidato());
					atuacaoCandidatos.add(atuacaoCand);
				}
			}
			// Controle das lista com registro adicionados e não adicionados
			for (Atuacao atuacao : areaAtuacoesAdicionados) {
				idsAtuacoes.add(atuacao.getId());
			}
			listarAreaAtuacoes(idsAtuacoes);
		} else {
			FacesMessagesUtil.addErrorMessage(" ", "Selecione pelo menos uma Área de atuação!");
		}
	}

	public void excluirAreaAtuacao() {
		Hibernate.initialize(areaAtuacaoExclusao);
		areaAtuacaoExclusao = (Atuacao) universalManager.get(Atuacao.class, areaAtuacaoExclusao.getId());
		List<Atuacao> listaAtuacaoAux = new ArrayList<Atuacao>();
		listaAtuacaoAux.addAll(areaAtuacoesAdicionados);
		List<Integer> idsAtuacoes = new ArrayList<Integer>();
		areaAtuacoesAdicionados = new ArrayList<Atuacao>();
		for (Atuacao atuacao : listaAtuacaoAux) {
			Hibernate.initialize(atuacao);
			atuacao = (Atuacao) universalManager.get(Atuacao.class, atuacao.getId());
			if (!atuacao.getId().equals(areaAtuacaoExclusao.getId())) {
				idsAtuacoes.add(atuacao.getId());
				areaAtuacoesAdicionados.add(atuacao);
			}
		}
		listarAreaAtuacoes(idsAtuacoes);
		boolean adicionado = false;
		if (getModel().getCandidato().getAtuacaoCandidatos() != null
				&& !getModel().getCandidato().getAtuacaoCandidatos().isEmpty()) {
			for (AtuacaoCandidato atuacaoCandidato : getModel().getCandidato().getAtuacaoCandidatos()) {
				if (atuacaoCandidato.getAtuacao().getId().equals(areaAtuacaoExclusao.getId())) {
					if (!atuacaoCandidatosExclusao.contains(atuacaoCandidato)) {
						atuacaoCandidatosExclusao.add(atuacaoCandidato);
						adicionado = true;
					}
				}
			}
		}
		if (!adicionado) {
			List<AtuacaoCandidato> listaAux = new ArrayList<AtuacaoCandidato>();
			listaAux.addAll(atuacaoCandidatos);
			atuacaoCandidatos = new ArrayList<AtuacaoCandidato>();
			for (AtuacaoCandidato atuacaoCandidato : listaAux) {
				if (!atuacaoCandidato.getAtuacao().getId().equals(areaAtuacaoExclusao.getId())) {
					atuacaoCandidatos.add(atuacaoCandidato);
				}
			}
		}
	}

	public void adicionarCargos() {
		if (cargos != null && cargos.length > 0) {
			List<Integer> idsCargos = new ArrayList<Integer>();
			principal: for (int i = 0; i < cargos.length; i++) {
				cargosAdicionados.add(cargos[i]);
				// Caso tenha registro na lista mas não na base
				boolean existe = false;
				if (candidatoCargos != null && !candidatoCargos.isEmpty()) {
					for (CandidatoCargo candidatoCargo : candidatoCargos) {
						if (candidatoCargo.getCargo().getId().equals(cargos[i].getId())) {
							existe = true;
						}
					}
					if (!existe) {
						CandidatoCargo candidatoCargo = new CandidatoCargo();
						candidatoCargo.setCargo(cargos[i]);
						candidatoCargo.setCandidato(getModel().getCandidato());
						candidatoCargos.add(candidatoCargo);
						continue principal;
					}
				}
				// Primeiro registro
				if (!existe) {
					CandidatoCargo candidatoCargo = new CandidatoCargo();
					candidatoCargo.setCargo(cargos[i]);
					candidatoCargo.setCandidato(getModel().getCandidato());
					candidatoCargos.add(candidatoCargo);
				}
			}
			// Controle das lista com registro adicionados e não adicionados
			for (Cargo cargo : cargosAdicionados) {
				idsCargos.add(cargo.getId());
			}
			listarCargos(idsCargos);
		} else {
			FacesMessagesUtil.addErrorMessage(" ", "Selecione pelo menos um cargo!");
		}
	}

	public void deleteComprovante() {
		try {
			comprovantes.remove(comprovanteExcluir);
			FacesMessagesUtil.addInfoMessage("Comprovante ", " excluído com sucesso!");

		} catch (Exception e) {
			ExcecaoUtil.tratarExcecao(e);
		}
	}

	public void deleteDocumento() {
		try {
			documentos.remove(documentoExcluir);
			FacesMessagesUtil.addInfoMessage("Documento ", " excluído com sucesso!");

		} catch (Exception e) {
			ExcecaoUtil.tratarExcecao(e);
		}
	}

	public void excluirCargo() {
		Hibernate.initialize(cargoExclusao);
		cargoExclusao = (Cargo) universalManager.get(Cargo.class, cargoExclusao.getId());
		List<Cargo> listaCargoAux = new ArrayList<Cargo>();
		listaCargoAux.addAll(cargosAdicionados);
		List<Integer> idsCargos = new ArrayList<Integer>();
		cargosAdicionados = new ArrayList<Cargo>();
		for (Cargo cargo : listaCargoAux) {
			Hibernate.initialize(cargo);
			cargo = (Cargo) universalManager.get(Cargo.class, cargo.getId());
			if (!cargo.getId().equals(cargoExclusao.getId())) {
				idsCargos.add(cargo.getId());
				cargosAdicionados.add(cargo);
			}
		}
		listarCargos(idsCargos);
		boolean adicionado = false;
		if (getModel().getCandidato().getCandidatoCargo() != null && !getModel().getCandidato().getCandidatoCargo().isEmpty()) {
			for (CandidatoCargo candidatoCargo : getModel().getCandidato().getCandidatoCargo()) {
				if (candidatoCargo.getCargo().getId().equals(cargoExclusao.getId())) {
					if (!candidatoCargosExclusao.contains(candidatoCargo)) {
						candidatoCargosExclusao.add(candidatoCargo);
						adicionado = true;
					}
				}
			}
		}
		if (!adicionado) {
			List<CandidatoCargo> listaAux = new ArrayList<CandidatoCargo>();
			listaAux.addAll(candidatoCargos);
			candidatoCargos = new ArrayList<CandidatoCargo>();
			for (CandidatoCargo candidatoCargo : listaAux) {
				if (!candidatoCargo.getCargo().getId().equals(cargoExclusao.getId())) {
					candidatoCargos.add(candidatoCargo);
				}
			}
		}
	}

	private boolean verificarCurso() throws Exception {
		InscricaoCurso inscricaoJaCadastrada = new InscricaoCurso();
		inscricaoJaCadastrada.setCandidato(new Candidato(getModel().getCandidato().getId()));
		inscricaoJaCadastrada.setCurso(new Curso(idCursoSelecionado));
		List<InscricaoCurso> listaInscricaoCadastrada = inscricaoCursoService.listarInscricoes(inscricaoJaCadastrada);
		for (InscricaoCurso ic : listaInscricaoCadastrada) {

			ic.setStatusUltimo(cursoService.ultimoStatusInscricao(ic).getStatus());
			if (ic.getStatusUltimo() != null && ic.getStatusUltimo().getId().equals(Status.CANCELADO)) {
				continue;
			} else {
				FacesMessagesUtil.addErrorMessage("Inscrição ", " para esse curso, já foi realizada!");
				return Boolean.FALSE;
			}

		}

		return true;
	}

	private void adicionarEsferasGovernos() {
		if (esferaGovernos != null && esferaGovernos.length > 0) {
			getModel().setInscricaoEsferas(new HashSet<EsferaGoverno>());
			for (EsferaGoverno esferaGoverno : esferaGovernos) {
				getModel().getInscricaoEsferas().add(esferaGoverno);
			}
		}
		esferaGovernos = null;
	}

	private boolean validarSituacaoStatus() {
		Curso curso = (Curso) universalManager.get(Curso.class, getModel().getCurso().getId());
		Candidato candidato = (Candidato) universalManager.get(Candidato.class, loginBean.getModel().getId());

		// Lista de espera por Regiao.
		// Possui Vagas
		// Candidato Mesma Região do Curso
		boolean homologacaoConfirmacaoChefe = false;
		boolean semHomologacao = false;
		boolean candidatoMesmaRegiao = false;
		boolean temVagas = false;
		boolean listaEsperaRegiao = false;
		boolean listaEsperaGeral = false;
		boolean listaEsperaNaoPossui = false;

		Situacao situacao = null;
		Status status = null;

		// verifica se qual a homologação.
		// Qualquer tipo de homologação que precise de comprovante entra
		// aki.
		if (curso.getHomologacaoCursos() == null || curso.getHomologacaoCursos().isEmpty()) {
			semHomologacao = true;
		} else {
			for (HomologacaoCurso hm : curso.getHomologacaoCursos()) {
				Homologacao h = hm.getHomologacao();
				if (h.getId().equals(Homologacao.CONFIRMACAO_CHEFE)) {
					homologacaoConfirmacaoChefe = true;
				}
			}

		}

		candidatoMesmaRegiao = candidatoMesmaRegiaoCurso(curso, candidato);

		// verifica qual tipo de espera
		if (curso.getOpcaoListaCandidato().getId().equals(OpcaoListaCandidato.LISTA_ESPERA_REGIAO)) {
			listaEsperaRegiao = true;
		} else if (curso.getOpcaoListaCandidato().getId().equals(OpcaoListaCandidato.LISTA_ESPERA_GERAL)) {
			listaEsperaGeral = true;
		} else if (curso.getOpcaoListaCandidato().getId().equals(OpcaoListaCandidato.NAO_POSSUI_LISTA_ESPERA)) {
			listaEsperaNaoPossui = true;
		}

		if (curso.getFlgPossuiOficina()) {
			temVagas = true;
		} else {
			// verifica se exite candidato cancelados. Caso exita, é somando a quantidade de inscritos para no caso de haver lista de espera
			// o
			// candidato que se inscreveu primeiro ter a prioridade. referente ao issue-43
			Integer quantidadeInscritosCancelados = cursoService.countInscritosCancelados(curso.getId());

			// verifica se tem vaga.
			if (curso.getVagas() != null && (quantidadeInscrito + quantidadeInscritosCancelados) < curso.getVagas()) {
				temVagas = true;
			}
		}

		// Faz as combinações para ver qual a situação e status do curso
		if (isParceiro || isInstrutor) {
			status = new Status(Status.PRESENCA_CONFIRMADA);
			situacao = new Situacao(Situacao.INSCRITO);
		} else {
			if (listaEsperaRegiao) {
				if (temVagas) {
					if (homologacaoConfirmacaoChefe) {
						if (candidatoMesmaRegiao) {
							situacao = new Situacao(Situacao.INSCRITO);
							status = new Status(Status.PRE_INSCRITO);
						} else {
							situacao = new Situacao(Situacao.ESPERA);
							status = new Status(Status.AGUARDANDO_VAGA);
						}
					} else {
						if (candidatoMesmaRegiao) {
							situacao = new Situacao(Situacao.INSCRITO);
							if (semHomologacao) {
								status = new Status(Status.AGUARDANDO_HOMOLOGACAO);
							} else {
								status = new Status(Status.AGUARDANDO_ACEITE_INSCRICAO);
							}
						} else if (!candidatoMesmaRegiao) {
							situacao = new Situacao(Situacao.ESPERA);
							status = new Status(Status.AGUARDANDO_VAGA);
						}
					}
				} else {
					situacao = new Situacao(Situacao.ESPERA);
					if (candidatoMesmaRegiao) {
						status = new Status(Status.AGUARDANDO_VAGA_PRIORIDADE);
					} else if (!candidatoMesmaRegiao) {
						status = new Status(Status.AGUARDANDO_VAGA);
					}
				}
			} else if (listaEsperaGeral) {
				if (temVagas) {
					if (homologacaoConfirmacaoChefe) {
						situacao = new Situacao(Situacao.INSCRITO);
						status = new Status(Status.PRE_INSCRITO);
					} else {
						situacao = new Situacao(Situacao.INSCRITO);

						if (semHomologacao) {
							status = new Status(Status.AGUARDANDO_HOMOLOGACAO);
						} else {
							status = new Status(Status.AGUARDANDO_ACEITE_INSCRICAO);
						}
					}
				} else {
					situacao = new Situacao(Situacao.ESPERA);
					status = new Status(Status.AGUARDANDO_VAGA);
				}
			} else if (listaEsperaNaoPossui) {
				if (!temVagas && !curso.getFlgPossuiOficina()) {
					FacesMessagesUtil.addErrorMessage(" ", " Não Existe vaga para o curso.");
					return Boolean.FALSE;
				} else {
					if (homologacaoConfirmacaoChefe) {
						situacao = new Situacao(Situacao.INSCRITO);
						status = new Status(Status.PRE_INSCRITO);
					} else {
						situacao = new Situacao(Situacao.INSCRITO);
						if (semHomologacao) {
							status = new Status(Status.AGUARDANDO_HOMOLOGACAO);
						} else {
							status = new Status(Status.AGUARDANDO_ACEITE_INSCRICAO);
						}
					}
				}
			}
		}

		// Seta A situacao da inscricao
		getModel().setSituacao(situacao);

		// Cria o Status Inscricao
		StatusInscricao statusInscricao = new StatusInscricao(getModel(), new Usuario(Constantes.USUARIO_SISFIE_ID), status,
				new Date());
		getModel().setStatusInscricoes(new HashSet<StatusInscricao>());
		getModel().getStatusInscricoes().add(statusInscricao);

		return true;

	}

	/**
	 * Método responsável por verificar se o candidato é da mesma região. Primeiro verifica os municípios e caso não os tenham, verifica-se
	 * as UFs
	 * 
	 * @param curso
	 * @param candidato
	 * @return TRUE se o candidato for da mesma região e FALSE o contrário
	 */
	private boolean candidatoMesmaRegiaoCurso(Curso curso, Candidato candidato) {
		if (curso.getMunicipioCursos() != null && !curso.getMunicipioCursos().isEmpty()) {
			for (MunicipioCurso municipioCurso : curso.getMunicipioCursos()) {
				if (municipioCurso.getMunicipio().getId().equals(candidato.getMunicipioOrgao().getId())) {
					return true;
				}
			}
		} else if (curso.getUfCursos() != null && !curso.getUfCursos().isEmpty()) {
			for (UfCurso ufCurso : curso.getUfCursos()) {
				if (ufCurso.getUf().getId().equals(candidato.getMunicipioOrgao().getUf().getId())) {
					return true;
				}
			}
		}
		return false;
	}

	public void anexarArquivo(FileUploadEvent event) {
		try {
			String fileName = ImagemUtil.criarNomeArquivo(event.getFile().getFileName(), loginBean.getModel());

			InscricaoComprovante comprovante = new InscricaoComprovante();
			comprovante.setDtCadastro(new Date());
			comprovante.setNomArquivo(fileName);
			comprovante.setNomTipo(event.getFile().getContentType());
			comprovante.setInscricaoCurso(new InscricaoCurso(inscricaoCursoSelecionado.getId()));
			comprovante.setImgComprovante(event.getFile().getContents());

			String os = System.getProperty("os.name");
			/* Descobre se linux ou windows */
			if (os.contains("win") || os.trim().toLowerCase().contains("windows") || os.trim().toLowerCase().contains("win")) {
				comprovante.setUrlImagem(Constantes.PATH_IMG_WINDOWS + fileName);
			} else {
				comprovante.setUrlImagem(Constantes.PATH_IMG_LINUX + fileName);
			}

			// Força a criação do arquivo no file system
			FileOutputStream fos = new FileOutputStream(new File(comprovante.getUrlImagem()));
			fos.write(event.getFile().getContents());
			fos.close();

			comprovantes.add(comprovante);

			FacesMessagesUtil.addInfoMessage(" ", "Arquivo " + event.getFile().getFileName() + " adicionado com sucesso!");
		} catch (Exception e) {
			ExcecaoUtil.tratarExcecao(e);
		}
	}

	public void anexarArquivoDocumentacao(FileUploadEvent event) {
		try {
			String fileName = ImagemUtil.criarNomeArquivo(event.getFile().getFileName(), loginBean.getModel());

			InscricaoDocumento documento = new InscricaoDocumento();
			documento.setDtCadastro(new Date());
			documento.setNomArquivo(fileName);
			documento.setNomTipo(event.getFile().getContentType());
			documento.setInscricaoCurso(new InscricaoCurso(inscricaoCursoSelecionado.getId()));
			documento.setImgDocumento(event.getFile().getContents());

			String os = System.getProperty("os.name");
			/* Descobre se linux ou windows */
			if (os.contains("win") || os.trim().toLowerCase().contains("windows") || os.trim().toLowerCase().contains("win")) {
				documento.setUrlImagem(Constantes.PATH_IMG_WINDOWS + fileName);
			} else {
				documento.setUrlImagem(Constantes.PATH_IMG_LINUX + fileName);
			}

			// Força a criação do arquivo no file system
			FileOutputStream fos = new FileOutputStream(new File(documento.getUrlImagem()));
			fos.write(event.getFile().getContents());
			fos.close();

			documentos.add(documento);

			FacesMessagesUtil.addInfoMessage(" ", "Arquivo " + event.getFile().getFileName() + " adicionado com sucesso!");
		} catch (Exception e) {
			ExcecaoUtil.tratarExcecao(e);
		}
	}

	@SuppressWarnings("unchecked")
	public String confirmarComprovante() {
		try {
			boolean comprovanteValido = false;
			for (InscricaoComprovante compro : comprovantes) {
				if (compro.getId() == null) {
					comprovanteValido = true;
					break;
				}
			}

			// se a validação for para os documentos
			boolean documentoValido = false;
			if (!comprovanteValido && inscricaoCursoSelecionado.getCurso().getFlgExigeDocumentacao()) {
				for (InscricaoDocumento documento : documentos) {
					if (documento.getId() == null) {
						documentoValido = true;
						break;
					}
				}
			}

			if (!comprovanteValido && !documentoValido) {
				FacesMessagesUtil.addErrorMessage(" ", " É necessário adicionar um comprovante e/ou documento.");
				return "";
			}

			// Valida informação pagamento
			if (comprovanteValido && mostrarInformacaoPagamento) {
				if (inscricaoInfoComplementar.getOrgao() == null || inscricaoInfoComplementar.getOrgao().getId() == null
						|| inscricaoInfoComplementar.getOrgao().getId() == 0) {
					FacesMessagesUtil.addErrorMessage("Órgão ", Constantes.CAMPO_OBRIGATORIO);
					return "";
				}
				if (inscricaoInfoComplementar.getNumeroEmpenho() == null
						|| inscricaoInfoComplementar.getNumeroEmpenho().isEmpty()) {
					FacesMessagesUtil.addErrorMessage("Número Empenho ", Constantes.CAMPO_OBRIGATORIO);
					return "";
				}
				if (inscricaoInfoComplementar.getEnderecoOrgaoComplemento() == null
						|| inscricaoInfoComplementar.getEnderecoOrgaoComplemento().isEmpty()) {
					FacesMessagesUtil.addErrorMessage("Endereço Órgão ", Constantes.CAMPO_OBRIGATORIO);
					return "";
				}
				if (inscricaoInfoComplementar.getCnpj() == null || inscricaoInfoComplementar.getCnpj().isEmpty()) {
					FacesMessagesUtil.addErrorMessage("CNPJ ", Constantes.CAMPO_OBRIGATORIO);
					return "";
				}
				String cnpj = inscricaoInfoComplementar.getCnpj().trim().replaceAll("[/()-.]", "");
				// Validar cnpj
				if (!CnpjUtil.isValidCNPJ(cnpj)) {
					FacesMessagesUtil.addErrorMessage("CNPJ ", "Inválido");
					return "";
				}
				// retirar mascaras
				inscricaoInfoComplementar.setCnpj(cnpj);
				// retira mascara
				if (inscricaoInfoComplementar.getContato() != null && !inscricaoInfoComplementar.getContato().trim().equals("")) {
					inscricaoInfoComplementar.setContato(inscricaoInfoComplementar.getContato().trim().replaceAll("[()-]", ""));
				}
				if (inscricaoInfoComplementar.getTelefoneFinanceiro() != null
						&& !inscricaoInfoComplementar.getTelefoneFinanceiro().trim().equals("")) {
					inscricaoInfoComplementar.setTelefoneFinanceiro(
							inscricaoInfoComplementar.getTelefoneFinanceiro().trim().replaceAll("[()-]", ""));
				}
				inscricaoInfoComplementar.setInscricaoCurso(inscricaoCursoSelecionado);
			} else {
				inscricaoInfoComplementar = null;
			}

			// Cria um status caso Novo. Caso o status seja diferente de aguardando validacao do comprovante
			StatusInscricao statusInscricao = null;
			if (!inscricaoCursoSelecionado.getStatusUltimo().getId().equals(Status.AGUARDANDO_VALIDACAO_COMPROVANTE)) {
				statusInscricao = new StatusInscricao(inscricaoCursoSelecionado, new Usuario(Constantes.USUARIO_SISFIE_ID),
						new Status(Status.AGUARDANDO_VALIDACAO_COMPROVANTE), new Date());
			}

			inscricaoCursoService.aguardandoComprovante(comprovantes, documentos, statusInscricao, inscricaoInfoComplementar);
			getSessionMap().put("confirmarComprovante", true);
			return redirect("/pages/inscricaoCurso.jsf");
		} catch (Exception e) {
			ExcecaoUtil.tratarExcecao(e);
		}

		return SUCCESS;
	}

	public String cancelarInscricao() {
		try {
			inscricaoCursoService.cancelarInscricao(inscricaoCursoSelecionado);
			verificarBotaoConfirmar();
			inscricaoCursoSelecionado = null;
			FacesMessagesUtil.addInfoMessage(" ", "Cancelamento realizado com sucesso!");
		} catch (Exception e) {
			FacesMessagesUtil.addErrorMessage(" ", "Erro ao cancelar Inscrição!");
			ExcecaoUtil.tratarExcecao(e);
		}
		return SUCCESS;
	}

	public String confirmarPresenca() {
		try {
			inscricaoCursoService.confirmarPresenca(inscricaoCursoSelecionado);
			verificarBotaoConfirmar();
			inscricaoCursoSelecionado = null;
			FacesMessagesUtil.addInfoMessage("Confirmação ", " Realizado com sucesso!");
		} catch (Exception e) {
			FacesMessagesUtil.addErrorMessage(" ", "Erro ao Confirmar Presença!");
			ExcecaoUtil.tratarExcecao(e);
		}
		return SUCCESS;
	}

	@Override
	public LazyDataModel<InscricaoCurso> getLazyDataModel() {
		if (lazyDataModel == null)

			lazyDataModel = new LazyDataModel<InscricaoCurso>() {

				private static final long serialVersionUID = 2595093445602418759L;

				@SuppressWarnings({ "rawtypes" })
				@Override
				public List<InscricaoCurso> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map filters) {
					try {
						int rowCount = inscricaoCursoService.countInscricoes(getModel()).intValue();
						lazyDataModel.setRowCount(rowCount);
						inscricoes = inscricaoCursoService.paginateInscricoes(first, pageSize, getModel());
						return inscricoes;
					} catch (Exception e) {
						ExcecaoUtil.tratarExcecao(e);
					}
					return null;
				}
			};
		return lazyDataModel;
	}

	@SuppressWarnings("unchecked")
	public String saveDadosInicias() {
		try {

			if (validarCampos()) {
				return "";
			}
			retirarMascaras();

			if (orgaoSelecionado != null && orgaoSelecionado.getId() == null) {
				getModel().getCandidato().setOrgao(null);
			} else {
				getModel().getCandidato().setOrgao(orgaoSelecionado);
			}
			getModel().getCandidato().setMunicipioEndereco(new Municipio(idMunicipioEnderecoSelecionado));
			getModel().getCandidato().setMunicipioOrgao(new Municipio(idMunicipioSelecionado));
			getModel().getCandidato().getMunicipioEndereco().setUf(new Uf(idUfEnderecoSelecionado));
			getModel().getCandidato().getMunicipioOrgao().setUf(new Uf(idUfOrgaoSelecionado));
			inscricaoCursoService.salvarCandidato(getModel().getCandidato(), atuacaoCandidatos, candidatoCargos,
					candidatoCargosExclusao, atuacaoCandidatosExclusao);
			getSessionMap().put("saveDados", true);
			return redirect("/pages/inscricaoCurso.jsf");
		} catch (Exception e) {
			ExcecaoUtil.tratarExcecao(e);
			return ERROR;
		}
	}

	@SuppressWarnings("unchecked")
	public String saveCurso() {
		try {

			if (cursoSelecionado.getFlgExigeDocumentacao() && !isInstrutor) {
				if (documentos == null || documentos.isEmpty()) {
					FacesMessagesUtil.addErrorMessage("Documentação ",
							"O curso exige documentos comprobatórios conforme descrito no edital.");
					return "";
				}
			}

			if (!validarCandidatoComplemento() && !isInstrutor) {
				return "";
			}
			if (!verificarCurso()) {
				return "";
			}

			// se tiver valido preenche situação e status do model
			quantidadeInscrito = inscricaoCursoService.retornarQuantidadeInscritos(idCursoSelecionado);
			if (!validarSituacaoStatus()) {
				return "";
			}

			adicionarEsferasGovernos();

			// Adiciona o email do chefe. Caso tenha sido escolhido essa opção no cadastro do curso.
			Candidato candidato = null;
			if (emailResponsavel != null && !emailResponsavel.isEmpty()) {
				// Seta o email do chefe e clona o registro
				Candidato candidatoAux = (Candidato) universalManager.get(Candidato.class, loginBean.getModel().getId());
				candidatoAux.setEmailChefe(emailResponsavel);
				candidato = (Candidato) candidatoAux.clone();
			}

			// Se candidatoComplemento não tiver candidato setado, que tiver que não tinha dados Obrigatorio a ser populado. Sendo assim
			// seto null no objeto.
			if (candidatoComplemento == null || candidatoComplemento.getCandidato() == null
					|| candidatoComplemento.getCandidato().getId() == null) {
				candidatoComplemento = null;
			}

			// Se for o primeiro a se inscrever no curso mudar o status do curso para em andamento
			StatusCurso statusCurso = null;
			if (quantidadeInscrito == 0) {
				statusCurso = new StatusCurso();
				statusCurso.setCurso(new Curso(idCursoSelecionado));
				statusCurso.setDtAtualizacao(new Date());
				statusCurso.setStatus(new Status(Status.EM_ANDAMENTO));
				statusCurso.setUsuario(new Usuario(Constantes.USUARIO_SISFIE_ID));
			}

			getModel().setCurso(new Curso(idCursoSelecionado));
			getModel().setFlgParceiro(isParceiro);
			getModel().setFlgInstrutor(isInstrutor);

			if (getModel().getId() == null) {
				getModel().setDtCadastro(new Date());
			}

			if (cursoSelecionado.getFlgPossuiOficina()) {
				if (cursoSelecionado.getCargaHoraria().equals(totalCargaHoraria)) {
					inscricaoCursoService.realizarInscricaoCurso(getModel(), candidato, candidatoComplemento, statusCurso,
							listaGradeOficinas, documentos);
				} else {
					FacesMessagesUtil.addErrorMessage("Inscrição não Realizada!",
							"Para realizar a inscrição é necessário ter o total da carga horária = "
									+ cursoSelecionado.getCargaHoraria());
					return "";
				}
			} else {
				inscricaoCursoService.realizarInscricaoCurso(getModel(), candidato, candidatoComplemento, statusCurso,
						documentos);
			}

			getSessionMap().put("saveIscricao", true);
			return redirect("/pages/inscricaoCurso.jsf");
		} catch (Exception e) {
			ExcecaoUtil.tratarExcecao(e);
			return ERROR;
		}
	}

	public boolean validarCandidatoComplemento() {
		// validar email chefe
		Candidato candidato = (Candidato) universalManager.get(Candidato.class, loginBean.getModel().getId());
		if (mostrarEmailResponsavel) {
			if (!emailResponsavel.contains("@") || !emailResponsavel.contains(".")) {
				FacesMessagesUtil.addErrorMessage("Email Responsável", "Inválida");
				return false;
			}
			if (!emailResponsavel.trim().equals(emailResponsavelConfirmacao)) {
				FacesMessagesUtil.addErrorMessage("Confirmação do Email do Responsável ", " Não Confere");
				return false;
			}
		}

		// validar Dados Bancario
		if (mostrarDadosBancario) {
			candidatoComplemento.setCandidato(new Candidato(candidato.getId()));
			if (candidatoComplemento.getBanco() == null || candidatoComplemento.getBanco().trim().isEmpty()) {
				FacesMessagesUtil.addErrorMessage("Número Banco ", Constantes.CAMPO_OBRIGATORIO);
				return false;
			}
			if (candidatoComplemento.getAgencia() == null || candidatoComplemento.getAgencia().trim().isEmpty()) {
				FacesMessagesUtil.addErrorMessage("Agência ", Constantes.CAMPO_OBRIGATORIO);
				return false;
			}
			if (candidatoComplemento.getContaCorrente() == null || candidatoComplemento.getContaCorrente().trim().isEmpty()) {
				FacesMessagesUtil.addErrorMessage("Conta Corrente ", Constantes.CAMPO_OBRIGATORIO);
				return false;
			}
		}
		// validar Nivel Ensino
		if (mostrarNivelEnsino) {
			candidatoComplemento.setCandidato(new Candidato(candidato.getId()));
			if (candidatoComplemento.getNomeInstituicao() == null || candidatoComplemento.getNomeInstituicao().trim().isEmpty()) {
				FacesMessagesUtil.addErrorMessage("Nome da Instituição ", Constantes.CAMPO_OBRIGATORIO);
				return false;
			}
			if (candidatoComplemento.getNomeCurso() == null || candidatoComplemento.getNomeCurso().trim().isEmpty()) {
				FacesMessagesUtil.addErrorMessage("Curso de Formação: ", Constantes.CAMPO_OBRIGATORIO);
				return false;
			}
			if (candidatoComplemento.getCodEducacional() == null || candidatoComplemento.getCodEducacional().trim().isEmpty()) {
				FacesMessagesUtil.addErrorMessage("Nível Escolaridade ", Constantes.CAMPO_OBRIGATORIO);
				return false;
			}
		}
		// validar Experiencia Profissional
		if (mostrarExperienciaProfissional) {
			candidatoComplemento.setCandidato(new Candidato(candidato.getId()));
			if (candidatoComplemento.getExperienciaProfissionalAnterior() == null
					|| candidatoComplemento.getExperienciaProfissionalAnterior().trim().isEmpty()) {
				FacesMessagesUtil.addErrorMessage("Experiência Anterior ", Constantes.CAMPO_OBRIGATORIO);
				return false;
			}
			if (candidatoComplemento.getExperienciaProfissionalAtual() == null
					|| candidatoComplemento.getExperienciaProfissionalAtual().trim().isEmpty()) {
				FacesMessagesUtil.addErrorMessage("Experiência Atual ", Constantes.CAMPO_OBRIGATORIO);
				return false;
			}
		}
		return true;
	}

	@SuppressWarnings("unchecked")
	public boolean validarCampos() throws Exception {
		// validar se existe (@ ou -) no nome completo
		if (getModel().getCandidato().getNome() != null && !getModel().getCandidato().getNome().isEmpty()) {
			for (int i = 0; i < getModel().getCandidato().getNome().length(); i++) {
				Character letra = getModel().getCandidato().getNome().charAt(i);
				switch (letra.charValue()) {
				case '@':
				case '-':
					FacesMessagesUtil.addErrorMessage("Caracteres inválidos!", "Não é permitido '@' nem '-' no mome completo.");
					return true;
				default:
					break;
				}
			}
		}

		// Validar email institucuional unique
		if (alterarEmail) {
			Candidato candidato = new Candidato();
			candidato.setEmailInstitucional(getModel().getCandidato().getEmailInstitucional().trim());
			List<Candidato> lista = universalManager.listBy(candidato, false);
			if (lista != null && !lista.isEmpty() && !lista.get(0).getId().equals(getModel().getCandidato().getId())) {
				FacesMessagesUtil.addErrorMessage("Email Institucional ", " Já cadastrado");

			}
			if (!getModel().getCandidato().getEmailInstitucional().trim().equals(confirmacaoEmail)) {
				FacesMessagesUtil.addErrorMessage("Confirmação do Email Institucional ", "Inválida");
				return true;
			}
		}
		if (orgaoSelecionado == null || orgaoSelecionado.getId() == null) {
			FacesMessagesUtil.addErrorMessage("Orgão ", Constantes.CAMPO_OBRIGATORIO);
			return true;
		}

		// valida se tem cpf ou doc estrangeiro
		if (getModel().getCandidato().getNacionalidadeBrasil()) {
			if (getModel().getCandidato().getCpf() == null || getModel().getCandidato().getCpf().isEmpty()) {
				FacesMessagesUtil.addErrorMessage("CPF ", Constantes.CAMPO_OBRIGATORIO);
				return true;
			}
			String cpf = getModel().getCandidato().getCpf().trim().replaceAll("[()-.]", "");
			// Validar CPF
			if (!CpfUtil.isValidCPF(cpf)) {
				FacesMessagesUtil.addErrorMessage("CPF ", "Inválido");
				return true;
			}

		} else {
			if (getModel().getCandidato().getDocEstrangeiro() == null
					|| getModel().getCandidato().getDocEstrangeiro().isEmpty()) {
				FacesMessagesUtil.addErrorMessage("Documento Estrangeiro ", Constantes.CAMPO_OBRIGATORIO);
				return true;
			}
		}
		return false;
	}

	public void retirarMascaras() {
		// retira mascara
		if (getModel().getCandidato().getCep() != null && !getModel().getCandidato().getCep().trim().equals("")) {
			getModel().getCandidato().setCep(getModel().getCandidato().getCep().trim().replaceAll("[()-.]", ""));
		}
		// retira mascara
		if (getModel().getCandidato().getTelCelular() != null && !getModel().getCandidato().getTelCelular().trim().equals("")) {
			getModel().getCandidato().setTelCelular(getModel().getCandidato().getTelCelular().trim().replaceAll("[()-]", ""));
		}
		// retira mascara
		if (getModel().getCandidato().getTelComercial() != null
				&& !getModel().getCandidato().getTelComercial().trim().equals("")) {
			getModel().getCandidato().setTelComercial(getModel().getCandidato().getTelComercial().trim().replaceAll("[()-]", ""));
		}
		// retira mascara
		if (getModel().getCandidato().getTelResidencial() != null
				&& !getModel().getCandidato().getTelResidencial().trim().equals("")) {
			getModel().getCandidato()
					.setTelResidencial(getModel().getCandidato().getTelResidencial().trim().replaceAll("[()-]", ""));
		}
		// retira mascara
		if (getModel().getCandidato().getCpf() != null && !getModel().getCandidato().getCpf().trim().equals("")) {
			getModel().getCandidato().setCpf(getModel().getCandidato().getCpf().trim().replaceAll("[()-.]", ""));
		}
	}

	public void changeUF(AjaxBehaviorEvent evt) {
		carregarMunicipios(idUfOrgaoSelecionado, Boolean.FALSE);
	}

	public void changeUFEndereco(AjaxBehaviorEvent evt) {
		carregarMunicipios(idUfEnderecoSelecionado, Boolean.TRUE);
	}

	@SuppressWarnings("unchecked")
	private void carregarMunicipios(Integer idUf, Boolean isEndereco) {
		Municipio municipio = new Municipio();
		municipio.setUf(new Uf(idUf));
		try {
			if (isEndereco) {
				municipioEnderecos = universalManager.listBy(municipio);
			} else {
				municipioOrgaos = universalManager.listBy(municipio);
			}
		} catch (Exception e) {
			ExcecaoUtil.tratarExcecao(e);
		}
	}

	public List<Orgao> completeOrgao(String query) {
		List<Orgao> sugestoes = new ArrayList<Orgao>();
		try {
			sugestoes = orgaoService.pesquisarOrgao(query);
		} catch (Exception e) {
			ExcecaoUtil.tratarExcecao(e);
		}

		return sugestoes;

	}

	private void carregarOutrosCursos(Curso curso) throws Exception {

		outrosCursos = cursoService.carregarOutrosCursos(curso);
	}

	@SuppressWarnings("unchecked")
	public void popularDadosCurso(AjaxBehaviorEvent evt) {
		try {
			// verifica qual Aba vai mostrar do dados Obrigatorios
			if (idCursoSelecionado != null && idCursoSelecionado != 0) {

				cursoSelecionado = (Curso) universalManager.get(Curso.class, idCursoSelecionado);
				
				isInstrutor = false;
				if (mapaInstrutorCurso != null && !mapaInstrutorCurso.isEmpty()){
					for (Entry<Curso, Boolean> entry : mapaInstrutorCurso.entrySet()) {
						if (entry.getKey().getId().equals(cursoSelecionado.getId())){
							isInstrutor = entry.getValue();
							break;
						}
					}
				}

				if (verificarColisaoCurso(cursoSelecionado)) {
					idCursoSelecionado = null;
					cursoSelecionado = new Curso();
					return;
				}

				carregarOutrosCursos(cursoSelecionado);

				if (cursoSelecionado.getUfCursos() != null && !cursoSelecionado.getUfCursos().isEmpty()) {
					ufCursos = new ArrayList<UfCurso>(cursoSelecionado.getUfCursos());
				}
				if (cursoSelecionado.getMunicipioCursos() != null && !cursoSelecionado.getMunicipioCursos().isEmpty()) {
					municipioCursos = new ArrayList<MunicipioCurso>(cursoSelecionado.getMunicipioCursos());
				}

				if (cursoSelecionado.getAreaConhecimentoCursos() != null
						&& !cursoSelecionado.getAreaConhecimentoCursos().isEmpty()) {
					areaConhecimentoCursos = new ArrayList<AreaConhecimentoCurso>(cursoSelecionado.getAreaConhecimentoCursos());
				}

				mostrarEmailResponsavel = false;
				mostrarDadosBancario = false;
				mostrarExperienciaProfissional = false;
				mostrarNivelEnsino = false;

				for (HomologacaoCurso hm : cursoSelecionado.getHomologacaoCursos()) {
					if (hm.getHomologacao().getId().equals(Homologacao.CONFIRMACAO_CHEFE)) {
						mostrarEmailResponsavel = true;
						break;
					}
				}

				for (CandidatoPreenchimento cp : cursoSelecionado.getCandidatoPreenchimentos()) {
					if (cp.getCampoPreenchimento().getId().equals(CampoPreenchimento.DADOS_BANCARIO)) {
						mostrarDadosBancario = true;
						continue;
					}
					if (cp.getCampoPreenchimento().getId().equals(CampoPreenchimento.EXPERIENCIA_PROFISSIONAL)) {
						mostrarExperienciaProfissional = true;
						continue;
					}
					if (cp.getCampoPreenchimento().getId().equals(CampoPreenchimento.NIVEL_ENSINO)) {
						mostrarNivelEnsino = true;
						continue;
					}
				}

				// Carrega candidato Complemento
				CandidatoComplemento cc = new CandidatoComplemento();
				cc.setCandidato(new Candidato(loginBean.getModel().getId()));
				List<CandidatoComplemento> lista = universalManager.listBy(cc);
				if (lista != null && !lista.isEmpty()) {
					candidatoComplemento = lista.get(0);
				}
				// Popula email do chefe
				emailResponsavel = loginBean.getModel().getEmailChefe();
			}
			listarEsferaGovenos();

			horarios = horarioService.listarHorarios(idCursoSelecionado);
		} catch (Exception e) {
			ExcecaoUtil.tratarExcecao(e);
		}
	}

	/**
	 * Método responsável por verificar se há alguma colisão entre turnos do Anexo A e B e horários referente ao Anexo C.
	 * 
	 * @author Wesley Marra
	 * @param curso
	 * @return 'True' se houver uma colisão e 'false' caso não haja.
	 */
	private boolean verificarColisaoCurso(Curso curso) {
		getModel().setCandidato((Candidato) universalManager.get(Candidato.class, getModel().getCandidato().getId()));
		if (getModel() != null && getModel().getCandidato() != null && getModel().getCandidato().getInscricoes() != null) {
			for (InscricaoCurso inscricaoCurso : getModel().getCandidato().getInscricoes()) {

				Status ultimoStatus = inscricaoCursoService.ultimoStatusInscricao(inscricaoCurso).getStatus();

				// Caso tenha algum turno null ou a inscrição tenha sido cancelada, não há necessidade de verificação
				if (ultimoStatus.getId().equals(Status.CANCELADO) || curso.getTurno() == null || curso.getTurno().getId() == null
						|| inscricaoCurso.getCurso().getTurno() == null || inscricaoCurso.getCurso().getTurno().getId() == null) {
					continue;
				}

				// Verificando se o Usuário esta tentando se inscrever em um curso que já tenha sido feito uma inscrição anteriormente.
				if (curso.getId().equals(inscricaoCurso.getCurso().getId())) {
					FacesMessagesUtil.addErrorMessage("Inscrição ", " Já foi realizado uma inscrição para o curso selecionado!");
					return Boolean.TRUE;
				}

				// Verificando colisões
				if ((curso.getDtRealizacaoInicio().getTime() >= inscricaoCurso.getCurso().getDtRealizacaoInicio().getTime()
						&& curso.getDtRealizacaoInicio().getTime() <= inscricaoCurso.getCurso().getDtRealizacaoFim().getTime())
						|| (curso.getDtRealizacaoFim().getTime() >= inscricaoCurso.getCurso().getDtRealizacaoInicio().getTime()
								&& curso.getDtRealizacaoFim().getTime() <= inscricaoCurso.getCurso().getDtRealizacaoFim()
										.getTime())) {
					if (!curso.getFlgPossuiOficina()) {
						if (!inscricaoCurso.getCurso().getFlgPossuiOficina()) {
							if (inscricaoCurso.getCurso().getTurno().getId().equals(curso.getTurno().getId())
									|| inscricaoCurso.getCurso().getTurno().getId().equals(Turno.AMBOS)
									|| curso.getTurno().getId().equals(Turno.AMBOS)) {
								FacesMessagesUtil.addErrorMessage("Colisão de Turnos",
										"Exite inscrição em outro curso que colidem com o dia e turno do curso selecionado!");
								return Boolean.TRUE;
							}
						}
					}
				}
			}
		}
		return Boolean.FALSE;
	}

	public void selecionarInscricao() {
		try {
			esferasGoverno = new ArrayList<EsferaGoverno>();

			if (inscricaoCursoSelecionado != null && inscricaoCursoSelecionado.getId() != null
					&& inscricaoCursoSelecionado.getId() != 0) {

				inscricaoCursoSelecionado = (InscricaoCurso) universalManager.get(InscricaoCurso.class,
						inscricaoCursoSelecionado.getId());
				Hibernate.initialize(inscricaoCursoSelecionado.getCurso());

				if (inscricaoCursoSelecionado.getCurso().getUfCursos() != null
						&& !inscricaoCursoSelecionado.getCurso().getUfCursos().isEmpty()) {
					ufCursos = new ArrayList<UfCurso>(inscricaoCursoSelecionado.getCurso().getUfCursos());
				}
				if (inscricaoCursoSelecionado.getCurso().getMunicipioCursos() != null
						&& !inscricaoCursoSelecionado.getCurso().getMunicipioCursos().isEmpty()) {
					municipioCursos = new ArrayList<MunicipioCurso>(inscricaoCursoSelecionado.getCurso().getMunicipioCursos());
				}

				if (inscricaoCursoSelecionado.getCurso().getAreaConhecimentoCursos() != null
						&& !inscricaoCursoSelecionado.getCurso().getAreaConhecimentoCursos().isEmpty()) {
					areaConhecimentoCursos = new ArrayList<AreaConhecimentoCurso>(
							inscricaoCursoSelecionado.getCurso().getAreaConhecimentoCursos());
				}

				if (inscricaoCursoSelecionado.getInscricaoEsferas() != null
						&& !inscricaoCursoSelecionado.getInscricaoEsferas().isEmpty()) {
					esferasGoverno = new ArrayList<EsferaGoverno>(inscricaoCursoSelecionado.getInscricaoEsferas());
				}
				inscricaoCursoSelecionado
						.setStatusUltimo(inscricaoCursoService.ultimoStatusInscricao(inscricaoCursoSelecionado).getStatus());

				comprovantes = inscricaoCursoService.carregarComprovantes(inscricaoCursoSelecionado);
				documentos = inscricaoCursoService.carregarDocumentacao(inscricaoCursoSelecionado);

				// Verifica se é para exibir informação de pagamento
				mostrarInformacaoPagamento = false;
				for (HomologacaoCurso hc : inscricaoCursoSelecionado.getCurso().getHomologacaoCursos()) {
					if (hc.getHomologacao().getId().equals(Homologacao.CONFIRMACAO_NOTA_EMPENHO)
							|| hc.getHomologacao().getId().equals(Homologacao.CONFIRMACAO_VIA_GRU)) {
						if (inscricaoCursoSelecionado.getCandidato().getOrgao() == null || !inscricaoCursoSelecionado
								.getCandidato().getOrgao().getId().equals(Orgao.INSTITUICAO_PARTICULAR)) {
							mostrarInformacaoPagamento = true;
						} else {
							mostrarInformacaoPagamento = false;
						}

						if (inscricaoCursoSelecionado.getInscricaoInfoComplemento() != null
								&& !inscricaoCursoSelecionado.getInscricaoInfoComplemento().isEmpty()) {
							inscricaoInfoComplementar = inscricaoCursoSelecionado.getInscricaoInfoComplemento().iterator().next();
						}
						break;
					}
				}
			}
			// Caso o curso exita oficinas, carrega-se a grade
			if (inscricaoCursoSelecionado.getCurso().getFlgPossuiOficina()) {
				listaInscricaoGrade = oficinaService.recuperarInscricaoGrade(inscricaoCursoSelecionado.getId());
				if (listaInscricaoGrade != null && !listaInscricaoGrade.isEmpty()) {
					Collections.sort(listaInscricaoGrade, new Comparator<InscricaoGrade>() {
						@Override
						public int compare(InscricaoGrade o1, InscricaoGrade o2) {
							return o1.getGradeOficina().getHorario().getDatHoraInicio()
									.compareTo(o2.getGradeOficina().getHorario().getDatHoraInicio());
						}
					});
				}
			}

			verificarBotaoConfirmar();
			verificarBotaoCancelar();

		} catch (Exception e) {
			ExcecaoUtil.tratarExcecao(e);
		}
	}

	/**
	 * Prazo limite para cancelamento pelo candidato é de 17 dias antes da data início do curso TODO : Rever isso com a esaf depois.
	 * 
	 * @throws ParseException
	 */
	private void verificarBotaoCancelar() throws ParseException {
		mostrarBotaoCancelar = false;
		if (inscricaoCursoSelecionado.getCurso().getFlgPossuiOficina()) {
			if (!inscricaoCursoSelecionado.getUltimoStatus().getStatus().getId().equals(Status.CANCELADO)
					&& DateUtil.diferencaEntreDatas(new Date(),
							inscricaoCursoSelecionado.getCurso().getDtRealizacaoInicio()) >= PRAZO_LIMITE_CANCELAMENTO) {
				mostrarBotaoCancelar = true;
			}
		} else {
			if (!inscricaoCursoSelecionado.getUltimoStatus().getStatus().getId().equals(Status.CANCELADO)) {
				mostrarBotaoCancelar = true;
			}
		}
	}

	public void limparCampo() {
		if (getModel().getCandidato().getNacionalidadeBrasil()) {
			getModel().getCandidato().setDocEstrangeiro(null);
		} else {
			getModel().getCandidato().setCpf(null);
		}
	}

	@Override
	public Map<String, String> getFilters() {
		return null;
	}

	@Override
	public InscricaoCurso createModel() {
		return new InscricaoCurso();
	}

	@Override
	public String getQualifiedName() {
		return "Inscrição";
	}

	@Override
	public boolean isFeminino() {
		return true;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public String getContext() {
		return context;
	}

	public void setContext(String context) {
		this.context = context;
	}

	public List<Uf> getUfOrgaos() {
		return UfOrgaos;
	}

	public void setUfOrgaos(List<Uf> ufOrgaos) {
		UfOrgaos = ufOrgaos;
	}

	public Integer getIdUfOrgaoSelecionado() {
		return idUfOrgaoSelecionado;
	}

	public void setIdUfOrgaoSelecionado(Integer idUfOrgaoSelecionado) {
		this.idUfOrgaoSelecionado = idUfOrgaoSelecionado;
	}

	public List<Municipio> getMunicipioOrgaos() {
		return municipioOrgaos;
	}

	public void setMunicipioOrgaos(List<Municipio> municipioOrgaos) {
		this.municipioOrgaos = municipioOrgaos;
	}

	public Integer getIdMunicipioSelecionado() {
		return idMunicipioSelecionado;
	}

	public void setIdMunicipioSelecionado(Integer idMunicipioSelecionado) {
		this.idMunicipioSelecionado = idMunicipioSelecionado;
	}

	public List<EsferaGoverno> getListaEsferaGovernos() {
		return listaEsferaGovernos;
	}

	public void setListaEsferaGovernos(List<EsferaGoverno> listaEsferaGovernos) {
		this.listaEsferaGovernos = listaEsferaGovernos;
	}

	public Integer getIdCursoSelecionado() {
		return idCursoSelecionado;
	}

	public void setIdCursoSelecionado(Integer idCursoSelecionado) {
		this.idCursoSelecionado = idCursoSelecionado;
	}

	public List<InscricaoCurso> getInscricoes() {
		return inscricoes;
	}

	public void setInscricoes(List<InscricaoCurso> inscricoes) {
		this.inscricoes = inscricoes;
	}

	public Boolean getFlgNacionalidade() {
		return flgNacionalidade;
	}

	public void setFlgNacionalidade(Boolean flgNacionalidade) {
		this.flgNacionalidade = flgNacionalidade;
	}

	public InscricaoCursoService getInscricaoCursoService() {
		return inscricaoCursoService;
	}

	public void setInscricaoCursoService(InscricaoCursoService inscricaoCursoService) {
		this.inscricaoCursoService = inscricaoCursoService;
	}

	public LoginBean getLoginBean() {
		return loginBean;
	}

	public void setLoginBean(LoginBean loginBean) {
		this.loginBean = loginBean;
	}

	public EsferaCursoService getEsferaCursoService() {
		return esferaCursoService;
	}

	public void setEsferaCursoService(EsferaCursoService esferaCursoService) {
		this.esferaCursoService = esferaCursoService;
	}

	public CandidatoBean getCandidatoBean() {
		return candidatoBean;
	}

	public void setCandidatoBean(CandidatoBean candidatoBean) {
		this.candidatoBean = candidatoBean;
	}

	public CursoService getCursoService() {
		return cursoService;
	}

	public void setCursoService(CursoService cursoService) {
		this.cursoService = cursoService;
	}

	public List<Municipio> getMunicipioEnderecos() {
		return municipioEnderecos;
	}

	public void setMunicipioEnderecos(List<Municipio> municipioEnderecos) {
		this.municipioEnderecos = municipioEnderecos;
	}

	public Integer getIdMunicipioEnderecoSelecionado() {
		return idMunicipioEnderecoSelecionado;
	}

	public void setIdMunicipioEnderecoSelecionado(Integer idMunicipioEnderecoSelecionado) {
		this.idMunicipioEnderecoSelecionado = idMunicipioEnderecoSelecionado;
	}

	public Integer getIdUfEnderecoSelecionado() {
		return idUfEnderecoSelecionado;
	}

	public void setIdUfEnderecoSelecionado(Integer idUfEnderecoSelecionado) {
		this.idUfEnderecoSelecionado = idUfEnderecoSelecionado;
	}

	public List<Uf> getUfEnderecos() {
		return UfEnderecos;
	}

	public void setUfEnderecos(List<Uf> ufEnderecos) {
		UfEnderecos = ufEnderecos;
	}

	public List<String> getIdsAreaAtuacao() {
		return idsAreaAtuacao;
	}

	public void setIdsAreaAtuacao(List<String> idsAreaAtuacao) {
		this.idsAreaAtuacao = idsAreaAtuacao;
	}

	public AreaAtuacaoDataModel getAreaAtuacaoDataModel() {
		return areaAtuacaoDataModel;
	}

	public void setAreaAtuacaoDataModel(AreaAtuacaoDataModel areaAtuacaoDataModel) {
		this.areaAtuacaoDataModel = areaAtuacaoDataModel;
	}

	public Atuacao[] getAreaAtuacoes() {
		return areaAtuacoes;
	}

	public void setAreaAtuacoes(Atuacao[] areaAtuacoes) {
		this.areaAtuacoes = areaAtuacoes;
	}

	public CargoDataModel getCargoDataModel() {
		return cargoDataModel;
	}

	public void setCargoDataModel(CargoDataModel cargoDataModel) {
		this.cargoDataModel = cargoDataModel;
	}

	public Cargo[] getCargos() {
		return cargos;
	}

	public void setCargos(Cargo[] cargos) {
		this.cargos = cargos;
	}

	public List<Atuacao> getAreaAtuacoesAdicionados() {
		return areaAtuacoesAdicionados;
	}

	public void setAreaAtuacoesAdicionados(List<Atuacao> areaAtuacoesAdicionados) {
		this.areaAtuacoesAdicionados = areaAtuacoesAdicionados;
	}

	public Atuacao getAreaAtuacaoExclusao() {
		return areaAtuacaoExclusao;
	}

	public void setAreaAtuacaoExclusao(Atuacao areaAtuacaoExclusao) {
		this.areaAtuacaoExclusao = areaAtuacaoExclusao;
	}

	public List<Cargo> getCargosAdicionados() {
		return cargosAdicionados;
	}

	public void setCargosAdicionados(List<Cargo> cargosAdicionados) {
		this.cargosAdicionados = cargosAdicionados;
	}

	public Cargo getCargoExclusao() {
		return cargoExclusao;
	}

	public void setCargoExclusao(Cargo cargoExclusao) {
		this.cargoExclusao = cargoExclusao;
	}

	public List<AtuacaoCandidato> getAtuacaoCandidatosExclusao() {
		return atuacaoCandidatosExclusao;
	}

	public void setAtuacaoCandidatosExclusao(List<AtuacaoCandidato> atuacaoCandidatosExclusao) {
		this.atuacaoCandidatosExclusao = atuacaoCandidatosExclusao;
	}

	public List<CandidatoCargo> getCandidatoCargosExclusao() {
		return candidatoCargosExclusao;
	}

	public void setCandidatoCargosExclusao(List<CandidatoCargo> candidatoCargosExclusao) {
		this.candidatoCargosExclusao = candidatoCargosExclusao;
	}

	public List<AtuacaoCandidato> getAtuacaoCandidatos() {
		return atuacaoCandidatos;
	}

	public void setAtuacaoCandidatos(List<AtuacaoCandidato> atuacaoCandidatos) {
		this.atuacaoCandidatos = atuacaoCandidatos;
	}

	public List<CandidatoCargo> getCandidatoCargos() {
		return candidatoCargos;
	}

	public void setCandidatoCargos(List<CandidatoCargo> candidatoCargos) {
		this.candidatoCargos = candidatoCargos;
	}

	public EsferaGovernoDataModel getEsferaGovernoDataModel() {
		return esferaGovernoDataModel;
	}

	public void setEsferaGovernoDataModel(EsferaGovernoDataModel esferaGovernoDataModel) {
		this.esferaGovernoDataModel = esferaGovernoDataModel;
	}

	public EsferaGoverno[] getEsferaGovernos() {
		return esferaGovernos;
	}

	public void setEsferaGovernos(EsferaGoverno[] esferaGovernos) {
		this.esferaGovernos = esferaGovernos;
	}

	public HomologacaoCursoService getHomologacaoCursoService() {
		return homologacaoCursoService;
	}

	public void setHomologacaoCursoService(HomologacaoCursoService homologacaoCursoService) {
		this.homologacaoCursoService = homologacaoCursoService;
	}

	public String getConfirmacaoEmail() {
		return confirmacaoEmail;
	}

	public void setConfirmacaoEmail(String confirmacaoEmail) {
		this.confirmacaoEmail = confirmacaoEmail;
	}

	public boolean isAlterarEmail() {
		return alterarEmail;
	}

	public void setAlterarEmail(boolean alterarEmail) {
		this.alterarEmail = alterarEmail;
	}

	public Orgao getOrgaoSelecionado() {
		return orgaoSelecionado;
	}

	public void setOrgaoSelecionado(Orgao orgaoSelecionado) {
		this.orgaoSelecionado = orgaoSelecionado;
	}

	public int getAbaId() {
		return abaId;
	}

	public void setAbaId(int abaId) {
		this.abaId = abaId;
	}

	public boolean isMostrarDadosBancario() {
		return mostrarDadosBancario;
	}

	public void setMostrarDadosBancario(boolean mostrarDadosBancario) {
		this.mostrarDadosBancario = mostrarDadosBancario;
	}

	public boolean isMostrarNivelEnsino() {
		return mostrarNivelEnsino;
	}

	public void setMostrarNivelEnsino(boolean mostrarNivelEnsino) {
		this.mostrarNivelEnsino = mostrarNivelEnsino;
	}

	public boolean isMostrarExperienciaProfissional() {
		return mostrarExperienciaProfissional;
	}

	public void setMostrarExperienciaProfissional(boolean mostrarExperienciaProfissional) {
		this.mostrarExperienciaProfissional = mostrarExperienciaProfissional;
	}

	public CandidatoComplemento getCandidatoComplemento() {
		return candidatoComplemento;
	}

	public void setCandidatoComplemento(CandidatoComplemento candidatoComplemento) {
		this.candidatoComplemento = candidatoComplemento;
	}

	public boolean isMostrarEmailResponsavel() {
		return mostrarEmailResponsavel;
	}

	public void setMostrarEmailResponsavel(boolean mostrarEmailResponsavel) {
		this.mostrarEmailResponsavel = mostrarEmailResponsavel;
	}

	public String getEmailResponsavel() {
		return emailResponsavel;
	}

	public void setEmailResponsavel(String emailResponsavel) {
		this.emailResponsavel = emailResponsavel;
	}

	public String getEmailResponsavelConfirmacao() {
		return emailResponsavelConfirmacao;
	}

	public void setEmailResponsavelConfirmacao(String emailResponsavelConfirmacao) {
		this.emailResponsavelConfirmacao = emailResponsavelConfirmacao;
	}

	public List<UfCurso> getUfCursos() {
		return ufCursos;
	}

	public void setUfCursos(List<UfCurso> ufCursos) {
		this.ufCursos = ufCursos;
	}

	public List<MunicipioCurso> getMunicipioCursos() {
		return municipioCursos;
	}

	public void setMunicipioCursos(List<MunicipioCurso> municipioCursos) {
		this.municipioCursos = municipioCursos;
	}

	public InscricaoCurso getInscricaoCursoSelecionado() {
		return inscricaoCursoSelecionado;
	}

	public void setInscricaoCursoSelecionado(InscricaoCurso inscricaoCursoSelecionado) {
		this.inscricaoCursoSelecionado = inscricaoCursoSelecionado;
	}

	public List<EsferaGoverno> getEsferasGoverno() {
		return esferasGoverno;
	}

	public void setEsferasGoverno(List<EsferaGoverno> esferasGoverno) {
		this.esferasGoverno = esferasGoverno;
	}

	public List<InscricaoComprovante> getComprovantes() {
		return comprovantes;
	}

	public void setComprovantes(List<InscricaoComprovante> comprovantes) {
		this.comprovantes = comprovantes;
	}

	public InscricaoComprovante getComprovanteExcluir() {
		return comprovanteExcluir;
	}

	public void setComprovanteExcluir(InscricaoComprovante comprovanteExcluir) {
		this.comprovanteExcluir = comprovanteExcluir;
	}

	public boolean isMostrarInformacaoPagamento() {
		return mostrarInformacaoPagamento;
	}

	public void setMostrarInformacaoPagamento(boolean mostrarInformacaoPagamento) {
		this.mostrarInformacaoPagamento = mostrarInformacaoPagamento;
	}

	public InscricaoInfoComplementar getInscricaoInfoComplementar() {
		return inscricaoInfoComplementar;
	}

	public void setInscricaoInfoComplementar(InscricaoInfoComplementar inscricaoInfoComplementar) {
		this.inscricaoInfoComplementar = inscricaoInfoComplementar;
	}

	public OrgaoService getOrgaoService() {
		return orgaoService;
	}

	public void setOrgaoService(OrgaoService orgaoService) {
		this.orgaoService = orgaoService;
	}

	public boolean isMostrarSelecaoOficina() {
		return mostrarSelecaoOficina;
	}

	public void setMostrarSelecaoOficina(boolean mostrarSelecaoOficina) {
		this.mostrarSelecaoOficina = mostrarSelecaoOficina;
	}

	public OficinaService getOficinaService() {
		return oficinaService;
	}

	public void setOficinaService(OficinaService oficinaService) {
		this.oficinaService = oficinaService;
	}

	public SelecaoOficinaService getSelecaoOficinaService() {
		return selecaoOficinaService;
	}

	public void setSelecaoOficinaService(SelecaoOficinaService selecaoOficinaService) {
		this.selecaoOficinaService = selecaoOficinaService;
	}

	public boolean isMostrarSelecaoPacote() {
		return mostrarSelecaoPacote;
	}

	public void setMostrarSelecaoPacote(boolean mostrarSelecaoPacote) {
		this.mostrarSelecaoPacote = mostrarSelecaoPacote;
	}

	public SelecaoPacoteService getSelecaoPacoteService() {
		return selecaoPacoteService;
	}

	public void setSelecaoPacoteService(SelecaoPacoteService selecaoPacoteService) {
		this.selecaoPacoteService = selecaoPacoteService;
	}

	public PacoteService getPacoteService() {
		return pacoteService;
	}

	public void setPacoteService(PacoteService pacoteService) {
		this.pacoteService = pacoteService;
	}

	public List<SelectItem> getCursos() {
		return cursos;
	}

	public void setCursos(List<SelectItem> cursos) {
		this.cursos = cursos;
	}

	public List<Curso> getOutrosCursos() {
		return outrosCursos;
	}

	public void setOutrosCursos(List<Curso> outrosCursos) {
		this.outrosCursos = outrosCursos;
	}

	public List<AreaConhecimentoCurso> getAreaConhecimentoCursos() {
		return areaConhecimentoCursos;
	}

	public void setAreaConhecimentoCursos(List<AreaConhecimentoCurso> areaConhecimentoCursos) {
		this.areaConhecimentoCursos = areaConhecimentoCursos;
	}

	public Curso getCursoSelecionado() {
		return cursoSelecionado;
	}

	public void setCursoSelecionado(Curso cursoSelecionado) {
		this.cursoSelecionado = cursoSelecionado;
	}

	public DistribuirService getDistribuirService() {
		return distribuirService;
	}

	public void setDistribuirService(DistribuirService distribuirService) {
		this.distribuirService = distribuirService;
	}

	public boolean isMostrarBotaoConfirmar() {
		return mostrarBotaoConfirmar;
	}

	public void setMostrarBotaoConfirmar(boolean mostrarBotaoConfirmar) {
		this.mostrarBotaoConfirmar = mostrarBotaoConfirmar;
	}

	public List<InscricaoDocumento> getDocumentos() {
		return documentos;
	}

	public void setDocumentos(List<InscricaoDocumento> documentos) {
		this.documentos = documentos;
	}

	public InscricaoDocumento getDocumentoExcluir() {
		return documentoExcluir;
	}

	public void setDocumentoExcluir(InscricaoDocumento documentoExcluir) {
		this.documentoExcluir = documentoExcluir;
	}

	public Integer getQuantidadeInscrito() {
		return quantidadeInscrito;
	}

	public void setQuantidadeInscrito(Integer quantidadeInscrito) {
		this.quantidadeInscrito = quantidadeInscrito;
	}

	public List<Horario> getHorarios() {
		return horarios;
	}

	public void setHorarios(List<Horario> horarios) {
		this.horarios = horarios;
	}

	public HorarioService getHorarioService() {
		return horarioService;
	}

	public void setHorarioService(HorarioService horarioService) {
		this.horarioService = horarioService;
	}

	public Integer getIdHorario() {
		return idHorario;
	}

	public void setIdHorario(Integer idHorario) {
		this.idHorario = idHorario;
	}

	public List<GradeOficina> getListaGradeOficinas() {
		return listaGradeOficinas;
	}

	public void setListaGradeOficinas(List<GradeOficina> listaGradeOficinas) {
		this.listaGradeOficinas = listaGradeOficinas;
	}

	public TurmaService getTurmaService() {
		return turmaService;
	}

	public void setTurmaService(TurmaService turmaService) {
		this.turmaService = turmaService;
	}

	public List<GradeOficina> getGradeOficinas() {
		return gradeOficinas;
	}

	public void setGradeOficinas(List<GradeOficina> gradeOficinas) {
		this.gradeOficinas = gradeOficinas;
	}

	public Integer getIdGradeOficina() {
		return idGradeOficina;
	}

	public void setIdGradeOficina(Integer idGradeOficina) {
		this.idGradeOficina = idGradeOficina;
	}

	public String getTextoConfirmacaoSelecaoOficina() {
		return textoConfirmacaoSelecaoOficina;
	}

	public void setTextoConfirmacaoSelecaoOficina(String textoConfirmacaoSelecaoOficina) {
		this.textoConfirmacaoSelecaoOficina = textoConfirmacaoSelecaoOficina;
	}

	public GradeOficina getGradeOficinaExclusao() {
		return gradeOficinaExclusao;
	}

	public void setGradeOficinaExclusao(GradeOficina gradeOficinaExclusao) {
		this.gradeOficinaExclusao = gradeOficinaExclusao;
	}

	public List<InscricaoGrade> getListaInscricaoGrade() {
		return listaInscricaoGrade;
	}

	public void setListaInscricaoGrade(List<InscricaoGrade> listaInscricaoGrade) {
		this.listaInscricaoGrade = listaInscricaoGrade;
	}

	public CandidatoService getCandidatoService() {
		return candidatoService;
	}

	public void setCandidatoService(CandidatoService candidatoService) {
		this.candidatoService = candidatoService;
	}

	public GradeOficina getGradeOficina() {
		return gradeOficina;
	}

	public void setGradeOficina(GradeOficina gradeOficina) {
		this.gradeOficina = gradeOficina;
	}

	public List<GradeOficina> getListaGradeOficinasVinculadas() {
		return listaGradeOficinasVinculadas;
	}

	public void setListaGradeOficinasVinculadas(List<GradeOficina> listaGradeOficinasVinculadas) {
		this.listaGradeOficinasVinculadas = listaGradeOficinasVinculadas;
	}

	public Integer getTotalCargaHoraria() {
		return totalCargaHoraria;
	}

	public void setTotalCargaHoraria(Integer totalCargaHoraria) {
		this.totalCargaHoraria = totalCargaHoraria;
	}

	public boolean isParceiro() {
		return isParceiro;
	}

	public void setParceiro(boolean isParceiro) {
		this.isParceiro = isParceiro;
	}

	public boolean isMostrarBotaoCancelar() {
		return mostrarBotaoCancelar;
	}

	public void setMostrarBotaoCancelar(boolean mostrarBotaoCancelar) {
		this.mostrarBotaoCancelar = mostrarBotaoCancelar;
	}

	public Map<Curso, Boolean> getMapaInstrutorCurso() {
		return mapaInstrutorCurso;
	}

	public void setMapaInstrutorCurso(Map<Curso, Boolean> mapaInstrutorCurso) {
		this.mapaInstrutorCurso = mapaInstrutorCurso;
	}

	public boolean isInstrutor() {
		return isInstrutor;
	}

	public void setInstrutor(boolean isInstrutor) {
		this.isInstrutor = isInstrutor;
	}
}
