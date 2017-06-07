package it.polito.tdp.seriea.model;

import java.util.List;

public class testModel {

	public static void main(String[] args) {

		Model m =new Model() ;
		List<Season> stagioni =m.setStagioni();
		m.caricaPartite(stagioni.get(0));
		System.out.println(m.getGrafo());

	}

}
