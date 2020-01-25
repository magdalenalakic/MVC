package com.example.demo.controller;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.KlinikaDTO;
import com.example.demo.dto.LekarDTO;
import com.example.demo.dto.PacijentDTO;
import com.example.demo.model.Klinika;
import com.example.demo.model.Lekar;
import com.example.demo.model.Pacijent;
import com.example.demo.model.Pregled;
import com.example.demo.model.SlobodniTermin;
import com.example.demo.service.KlinikaService;
import com.example.demo.service.LekarService;
import com.example.demo.service.PregledService;
import com.example.demo.service.SlobodniTerminService;

@RestController
@RequestMapping(value = "/api/klinike", produces = MediaType.APPLICATION_JSON_VALUE)
public class KlinikaController {
	@Autowired
	private KlinikaService klinikaService;
	@Autowired
	private LekarService lekarService;
	@Autowired
	private PregledService pregledService;
	@Autowired
	private SlobodniTerminService STService;
	


	@GetMapping(value = "/{id}")
	@CrossOrigin(origins = "http://localhost:3000")
	@PreAuthorize("hasAuthority('ADMIN_KLINIKE') or hasAuthority('LEKAR') or hasAuthority('ADMIN_KC')")
	public ResponseEntity<?> getKlinikaById(@PathVariable Long id) {
		System.out.println("Metoda find by id klinika: ");
		System.out.println(id);

		Klinika k = klinikaService.findOne(id);
		System.out.println("Pretraga klinike po ID");
		// studen must exist
		if (k == null) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		System.out.println(k.getNaziv() + " " + k.getId());
		return ResponseEntity.ok(new KlinikaDTO(k));
	}

//	@GetMapping(value = "/{id}")
//	@CrossOrigin(origins = "http://localhost:3000")
//	@PreAuthorize("hasAuthority('ADMIN_KLINIKE')")
//	public ResponseEntity<?> getKlinikaById(@PathVariable Long id) {
//		System.out.println("Metoda find by id klinika: ");
//		System.out.println(id);
//		
//		Klinika k = klinikaService.findOne(id);
//		System.out.println("Pretraga klinike po ID");
//		// studen must exist
//		if (k == null) {
//			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
//		}
//		System.out.println(k.getNaziv() + " " + k.getId());
//		return ResponseEntity.ok(new KlinikaDTO(k));
//	}

	@GetMapping(value = "/all")
	@PreAuthorize("hasAuthority('PACIJENT') or hasAuthority('ADMIN_KLINIKE')")
	public ResponseEntity<List<KlinikaDTO>> getAll() {

		List<Klinika> klinike = klinikaService.findAll();

		// convert students to DTOs
		List<KlinikaDTO> klinikaDTO = new ArrayList<>();
		for (Klinika k : klinike) {
			klinikaDTO.add(new KlinikaDTO(k));
		}

		return new ResponseEntity<>(klinikaDTO, HttpStatus.OK);
	}

	@GetMapping(value = "/findKlinikaByNaziv/{naziv}")
	@CrossOrigin(origins = "http://localhost:3000")
	@PreAuthorize("hasAuthority('ADMIN_KLINIKE')")
	public ResponseEntity<KlinikaDTO> getKlinikaByNaziv(@PathVariable String naziv) {
		System.out.println("find klinika by naziv");
		Klinika klinika = klinikaService.findByNaziv(naziv);
		if (klinika == null) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		System.out.println(klinika.getNaziv());
		return new ResponseEntity<>(new KlinikaDTO(klinika), HttpStatus.OK);
	}

	@GetMapping(value = "/findKlinikaByAdresa/{adresa}")
	public ResponseEntity<KlinikaDTO> getKlinikaByAdresa(@PathVariable String adresa) {
		System.out.println("find klinika by adresa");
		if (adresa.contains("%20"))
			adresa.replace("%20", " ");

		Klinika klinika = klinikaService.findByAdresa(adresa);

		if (klinika == null) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}

		System.out.println(klinika.getNaziv());
		return new ResponseEntity<>(new KlinikaDTO(klinika), HttpStatus.OK);
	}

	@PutMapping(path = "/update", consumes = "application/json")
	@CrossOrigin(origins = "http://localhost:3000")
	@PreAuthorize("hasAuthority( 'ADMIN_KLINIKE') or hasAuthority('ADMIN_KC')")
	public ResponseEntity<KlinikaDTO> updateKliniku(@RequestBody KlinikaDTO klinikaDTO) {

		// a student must exist
		System.out.println(" KLINIKa UPDRATE");
		Klinika klinika = klinikaService.findById(klinikaDTO.getId());

//		System.out.println("Lekar update: " + lekar.getEmail());
//		if (lekar == null) {
//			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
//		}

		klinika.setNaziv(klinikaDTO.getNaziv());
		klinika.setAdresa(klinikaDTO.getAdresa());
		klinika.setOpis(klinikaDTO.getOpis());
		klinika.setOcena(klinikaDTO.getOcena());

		klinika = klinikaService.save(klinika);
		System.out.println("Izmjenjena k: " + klinika);
		return new ResponseEntity<>(new KlinikaDTO(klinika), HttpStatus.OK);
	}

	@GetMapping(value = "/listaLekaraKlinika/{id}")
	@CrossOrigin(origins = "http://localhost:3000")
	@PreAuthorize("hasAuthority('ADMIN_KLINIKE') or hasAuthority('PACIJENT')")
	public ResponseEntity<List<LekarDTO>> getKlinikaLekari(@PathVariable Long id) {
		System.out.println("//////////////////// KLINIKA LISTA LEKARA /////////////////////////		");
		Klinika klinika = klinikaService.findById(id);
//		List<Lekar> listaSvihLekara =  lekarService.findAll();

		List<LekarDTO> lista = new ArrayList<>();

		for (Lekar l : klinika.getListaLekara()) {

//			if(klinika.getId() == l.getKlinika().getId()) {
			LekarDTO lDTO = new LekarDTO(l);
			lista.add(lDTO);
//			}
		}
//		for(Lekar ll : listaSvihLekara) {
//			if(!lista.contains(ll.getEmail())) {
//				System.out.println("Prazna listaaaaaaaaaaaaaaaaaaaaaaaa!!!!! ");
//				lista = null;
//			}
//		}

		return new ResponseEntity<>(lista, HttpStatus.OK);
	}

	// brisanje lekara
	@PostMapping(path = "/brisanjeLekara", consumes = "application/json")
	@CrossOrigin(origins = "http://localhost:3000")
	@PreAuthorize("hasAuthority('ADMIN_KLINIKE')")
	public ResponseEntity<String> brisanjeLekara(@RequestBody LekarDTO lekarDTO) {
		System.out.println("------------------------------------------------------");
		System.out.println("pocinje");
		// lekar koji se brise
		Lekar lekar = lekarService.findByEmail(lekarDTO.getEmail());
		System.out.println(lekar.getEmail());
		//List<Klinika> listaKlinika = klinikaService.findAll();
		System.out.println("Id LEKAR KLINIKA: " + lekar.getKlinika().getId());

		Long idLong = lekar.getKlinika().getId();

		Klinika klinika = klinikaService.findById(idLong);
		System.out.println("Klinika id ------------- : " + klinika.getId());

		if (klinika.getListaLekara().contains(lekar)) {
			//brisanje njegove liste slobodnih termina
			List<SlobodniTermin> listaST = STService.findAll();
			List<SlobodniTermin> listaSTkopija = listaST;
			
			for(SlobodniTermin s: listaSTkopija) {
				System.out.println("Slobodni termin L: " + s.getLekar().getIme());
				if(s.getLekar().equals(lekar)) {
					listaST.remove(s);
					STService.delete(s);
					
				}
			}	
			//brisanje liste pregleda
			List<Pregled> listaP = pregledService.findAll();
			List<Pregled> listaPkopija  = new ArrayList<Pregled>(listaP);
			System.out.println(pregledService.findAll().size());
			for(Pregled p: listaPkopija) {
					System.out.println("Preled: " + p.getLekar().getIme());
					if(p.getLekar().equals(lekar)) {
						System.out.println(listaP.size());
						
					
						Pregled pp = pregledService.findById(p.getId());
						listaP.remove(pp);
						System.out.println(listaP.size());
						
//						pregledService.delete(pp);
						pregledService.deleteById(pp.getId());
						
						
						
					//	lekar = lekarService.save(lekar);
						
						System.out.println("aaaaaaaaaaaaaaaaaaaaa");
					}
			}
			

			System.out.println(pregledService.findAll().size());
			System.out.println("dsadasdasdsadasads");
		}
			
		System.out.println("--------------*-*-*-*-*-*-*-*-*");
			Set<Lekar> lista = klinika.getListaLekara();
			System.out.println("------> LISTA LEKARA KLINIKE:  -----" );
			for(Lekar l: lista) {
				System.out.println(l.getEmail());
			}
			System.out.println("---------------------------------------");
			System.out.println("LEKAR kojeg brisem =============== " + lekar.getEmail());

			klinika.getListaLekara().remove(lekar);
			System.out.println("------> LISTA LEKARA KLINIKE NAKON BRISANJA :  -----" );
			for(Lekar l: klinika.getListaLekara()) {
				System.out.println(l.getEmail());
			}
			System.out.println("---------------------------------------");
			
			System.out.println(lekar.getEmail());
			
			Lekar ll = lekarService.findByEmail(lekarDTO.getEmail());
//			System.out.println(ll.getEmail());
//			System.out.println(lekar.getEmail());
			lekarService.delete(ll);
			
			System.out.println("/*****************   BAZA  *****************/");
			
			for(Lekar l: lekarService.findAll()) {
				System.out.println(l.getEmail());
			}
			System.out.println("/**********************************/");

		
		System.out.println("------------------------------------------------------");
		return new ResponseEntity<>("uspesno obrisan lekar !!!", HttpStatus.OK);
	}

	@GetMapping(value = "/pacijentiKlinike/{id}")
	@CrossOrigin(origins = "http://localhost:3000")
	@PreAuthorize("hasAuthority('LEKAR')")
	public ResponseEntity<List<PacijentDTO>> getPacijentiKlinike(@PathVariable Long id) {
		System.out.println("//////////////////// Klinika i lista pacijenata /////////////////////////		");
//		Klinika klinika = klinikaService.findById(id);

		List<Pacijent> listaPacijenataKlinike = klinikaService.findByIdKlinike(id);
		System.out.println("***********");

		for (Pacijent kp : listaPacijenataKlinike) {
			System.out.println(kp);

		}
		List<PacijentDTO> lista = new ArrayList<PacijentDTO>();
		for (Pacijent pp : listaPacijenataKlinike) {
			PacijentDTO pD = new PacijentDTO(pp);
			lista.add(pD);
		}
		System.out.println("*************");

		return new ResponseEntity<>(lista, HttpStatus.OK);
	}

	@PutMapping(path = "/oceni/{id}/{ocena}/{pregled_id}", consumes = "application/json")
	@CrossOrigin(origins = "http://localhost:3000")
	public ResponseEntity<KlinikaDTO> oceniKliniku(@PathVariable Long id, @PathVariable int ocena,
			@PathVariable Long pregled_id) {

		Klinika klinika = klinikaService.findById(id);
		int temp = klinika.getOcena();
		klinika.setOcena((temp + ocena) / 2);
		klinikaService.save(klinika);
		Pregled pregled = pregledService.findById(pregled_id);
		if (pregled.getStatus() == 3) {
			pregled.setStatus(4);
			pregledService.save(pregled);
		} else if (pregled.getStatus() == 5) {
			pregled.setStatus(6);
			pregledService.save(pregled);
		}

		return new ResponseEntity<>(new KlinikaDTO(klinika), HttpStatus.OK);
	}

}
