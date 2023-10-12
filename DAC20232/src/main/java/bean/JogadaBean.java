package bean;

import dao.JogadaDao;
import entidades.Jogada;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

@ManagedBean(name = "jogadaBean")
@ViewScoped
public class JogadaBean {
    private Jogada jogada = new Jogada();
    private List<Jogada> lista = new ArrayList<Jogada>();
    private Jogada jogadaEditavel = new Jogada();
    

    public void realizarOperacao() {
        try {
            jogada.setData(new Date());

            // Gerar jogadas aleatórias para jogador1 e jogador2
            jogada.setJogada1(gerarJogadaAleatoria());
            jogada.setJogada2(gerarJogadaAleatoria());

            // Calcular o resultado do jogo
            String resultado = calcularResultado(jogada.getJogada1(), jogada.getJogada2());
            jogada.setResultado(resultado);

            // Salvar a jogada no banco de dados
            JogadaDao.salvar(jogada);
            
            //pedido do professor no item i
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Sucesso.", "Sua Jogada Foi Salva! Resultado: " + jogada.getResultado()));
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro!", "Culpe o programador"));
        }
        // Reinciar a jogada
        jogada = new Jogada();
    }
    private String gerarJogadaAleatoria() {
        String[] jogadasPossiveis = { "Pedra", "Papel", "Tesoura" };
        int index = (int) (Math.random() * jogadasPossiveis.length);
        return jogadasPossiveis[index];
    }
    private String calcularResultado(String jogada1, String jogada2) {
        if (jogada1.equals(jogada2)) {
            return "Empate";
        } else if ((jogada1.equals("Pedra") && jogada2.equals("Tesoura"))
                || (jogada1.equals("Tesoura") && jogada2.equals("Papel"))
                || (jogada1.equals("Papel") && jogada2.equals("Pedra"))) {
            return "Jogador 1";
        } else {
            return "Jogador 2";
        }
    }
    public void carregarJogadaParaEdicao(Integer id) {
        jogadaEditavel = JogadaDao.acharPorId(id);
    }
   
    public void salvarEdicao() {
        if (jogadaEditavel != null) {
            JogadaDao.editar(jogadaEditavel);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Sucesso", "Suas Edições Foram Salvas!"));
            
            // Atualize a lista manualmente após salvar as edições
            lista = JogadaDao.listar();
        }
        jogadaEditavel = null;
    } 
    public void excluirJogada(Integer id) {
        Jogada jogadaParaExcluir = JogadaDao.acharPorId(id);
        if (jogadaParaExcluir != null) {
            JogadaDao.excluir(jogadaParaExcluir);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Sucesso", "Sua Jogada Foi Excluída!"));
        }
    }
    public Jogada getJogada() {
        return jogada;
    }
    public void setJogada(Jogada jogada) {
        this.jogada = jogada;
    }
    public List<Jogada> getLista() {
        if (lista.isEmpty()) {
            lista = JogadaDao.listar();
        }
        return lista;
    }
    public void setLista(List<Jogada> lista) {
        this.lista = lista;
    }
    
    public JogadaBean() {
        jogadaEditavel = new Jogada();
    }
    public Jogada getJogadaEditavel() {
        return jogadaEditavel;
    }

    public void setJogadaEditavel(Jogada jogadaEditavel) {
        this.jogadaEditavel = jogadaEditavel;
    }
    public Map<String, Integer> calcularNumeroJogadas() {
        Map<String, Integer> estatisticas = new HashMap<>();
        
        int pedraCount = 0;
        int papelCount = 0;
        int tesouraCount = 0;
        
        for (Jogada j : lista) {
            if ("Pedra".equals(j.getJogada1())) {
                pedraCount++;
            } else if ("Papel".equals(j.getJogada1())) {
                papelCount++;
            } else if ("Tesoura".equals(j.getJogada1())) {
                tesouraCount++;
            }         
            if ("Pedra".equals(j.getJogada2())) {
                pedraCount++;
            } else if ("Papel".equals(j.getJogada2())) {
                papelCount++;
            } else if ("Tesoura".equals(j.getJogada2())) {
                tesouraCount++;
            }
        }        
        estatisticas.put("Pedra", pedraCount);
        estatisticas.put("Papel", papelCount);
        estatisticas.put("Tesoura", tesouraCount);
        
        return estatisticas;
    }
}