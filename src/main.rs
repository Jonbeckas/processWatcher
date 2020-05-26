#[macro_use] extern crate log;
#[macro_use] extern crate serde_derive;
extern crate serde;
extern crate serde_json;
use std::fs::File;
use std::io::{BufReader};
use std::error::Error;
use std::process::exit;
use std::path::{Path, PathBuf};
use std::{fs, thread, time};
use std::borrow::Borrow;
use crate::structs::{Config, NEWJSON};
use crate::processes::KeepAlive;

mod structs;
mod processes;
static mut KEEP_ALIVES: Vec<KeepAlive> = Vec::new();



fn main(){
    env_logger::init();
    info!("ProcessWatcher started");
    let path = get_path();
    let path:&Path =path.borrow();
    info!("Checking Configuration path: {}",path.display());
    loop {
        match create_file(path) {
            Err(e) => {
                error!("{:?}", e.to_string());
                exit(1)
            },
            _ => {}
        }
        let config: Config;
        match read_data_from_json(path) {
            Ok(jso) => {
                match run_json(jso.clone(),jso.clone().attemps) {
                    Ok(()) =>(),
                    Err(e) => error!("{}",e.to_string())
                }
                config = jso.clone();
            },
            Err(e) => {
                error!("Error while parsing the json: {:?}", e.to_string());
                exit(1);
            }
        };
        if config.refresh.is_none() {
            error!("No attribute 'refresh' given");
            exit(1);
        }
        thread::sleep(time::Duration::from_secs(config.refresh.unwrap()));
    }

}
fn run_json(json:Config,attempts:Option<u64>)-> Result<(),Box<dyn Error>> {
    for js in json.clone().work {
        if !js.procname.is_none() && js.procname.clone().unwrap() !="" {
            let procname = js.procname.unwrap();
            if processes::check_running(procname.clone()) {
                info!("{} is running",procname.clone());
                if !js.is_running.is_none() {
                    info!("Start {}",procname.clone());
                    let is_running = js.is_running.unwrap();
                    match processes::start_process(is_running.clone()) {
                        Ok(res)=> info!("The task  '{}' for the case 'is running' from {} returned: {}",is_running.clone(),procname.clone(),res.to_string().replace("\n","\\n")),
                        Err(e) => error!("The task  '{}' for the case 'is running' from {} returned: {}",is_running.clone(),procname.clone(),e.to_string().replace("\n","\\n"))
                    };
                }
            } else {
                info!("{} is not running!",procname.clone());
                if !js.not_running.is_none() {
                    info!("Start {}",js.not_running.clone().unwrap());
                    match processes::start_process(js.not_running.clone().unwrap()) {
                        Ok(res)=> info!("The task  '{}' for the case 'is not running' from {} returned: {}",js.not_running.clone().unwrap(),procname.clone(),res.to_string().replace("\n","\\n")),
                        Err(e) => error!("The task  '{}' for the case 'is not running' from {} returned: {}",js.not_running.clone().unwrap(),procname.clone(),e.to_string().replace("\n","\\n"))
                    };
                }
            }
        }
        if !js.linkconf.is_none() && js.linkconf.clone().unwrap() !="" {
            let mut path_buf = PathBuf::new();
            path_buf.push(js.linkconf.unwrap());
            let path:&Path = path_buf.as_path();
            match create_file(path) {
                Err(e)=> error!("Cannot create file: {}",e.to_string()),
                _ => {}
            }
            match read_data_from_json(path) {
                Ok(jso)=> match run_json(jso,json.clone().attemps) {
                    Ok(())=>(),
                    Err(e)=> error!("ERR{}",e.to_string())
                }
                Err(e)=> error!("Error while parsing the childconf {} : {:?}", path.display(),e.to_string())
            }
        }
        if !js.keepalive.is_none() && js.keepalive.clone().unwrap() !="" {
            let mut index:isize =-1;
            let folder =  std::env::current_exe().ok().expect("Error while open configuration Folder");
            let folder = folder.parent().unwrap();
            let mut path_buf = PathBuf::new();
            path_buf.push(folder.parent().unwrap());
            path_buf.push("logs");
            path_buf.push(js.keepalive.clone().unwrap());
            path_buf.set_extension("log");
            let path = path_buf.as_path();
            unsafe {
                for (x,_i) in KEEP_ALIVES.iter().enumerate() {
                    if KEEP_ALIVES.get(x).unwrap().params.lock().unwrap().command == js.keepalive.clone().unwrap() {
                        index = x as isize;
                    }
                }

                if index != -1 {
                    KEEP_ALIVES.get(index as usize).unwrap().params.lock().unwrap().reset();
                    KEEP_ALIVES.get(index as usize).unwrap().params.lock().unwrap().restart();
                } else {
                    if attempts.is_none() {
                        error!("No attribute 'attempts' found");
                        exit(1);
                    }
                    let mut keep_alive = KeepAlive::new(attempts.unwrap(),js.keepalive.clone().unwrap());
                    keep_alive.start();
                    unsafe {
                        KEEP_ALIVES.push(keep_alive)
                    }
                    info!("Start KeepAlive Task for {}. Append logs to {}", js.keepalive.clone().unwrap(), path.display() )
                }
            }

        }
    }
    Ok(())
}

fn get_path()-> Box<Path> {
    let folder =  std::env::current_exe().ok().expect("Error while open configuration Folder");
    let folder = folder.parent().unwrap();
    let mut path_buf = PathBuf::new();
    path_buf.push(folder.parent().unwrap());
    path_buf.push("config");
    path_buf.set_extension("json");
    return Box::from(path_buf.as_path());
}

fn create_file(path:&Path) ->Result<(),Box<dyn Error>>{
    std::fs::create_dir_all(path.parent().unwrap())?;
    if !path.exists() {
        File::create(path)?;
        fs::write(path,NEWJSON)?;
        info!("Configuration file at {} does not exist, creating one", path.to_str().unwrap());
    }
    Ok(())
}



fn read_data_from_json(path:&Path)-> Result<Config,Box<dyn Error>> {
    let file = File::open(path)?;
    let reader = BufReader::new(file);

    // Read the JSON contents of the file as an instance of `User`.
    let u:Config = serde_json::from_reader(reader)?;
    Ok(u)
}



