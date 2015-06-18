package com.hybridplay.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.ParticleEmitter;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;

public class HPGame extends ApplicationAdapter implements InputProcessor {

	// BRIDGE OUTSIDE LIBGDX (ANDROID UNIVERSE)
	GameResolver gameResolver;

	/////////////////////////////////////// HP GAME VARS
	SpriteBatch batch;
	Texture img;
	BitmapFont font;

	ParticleEffect hpEffect;
	Array<ParticleEmitter> emitters;
	
	GameState gameState = GameState.START;
	
	/////////////////////////////////////// HP SENSOR
	Texture sTXL, sTXR, sTYL, sTYR;
	Texture sTXLON, sTXRON, sTYLON, sTYRON;
	
	public enum GameState {
	    MENU, INIT, START, UPDATE, GAMEOVER, PAUSE, EXIT, WIN;
	}

	public HPGame(GameResolver gr){
		this.gameResolver = gr;
	}

	@Override
	public void create(){
		Gdx.input.setInputProcessor(this);
		
		// INIT HP Sensor graphic visualizer
		sTXL = new Texture("img/izquierda_off.png");
		sTXR = new Texture("img/derecha_off.png");
		sTYL = new Texture("img/arriba_off.png");
		sTYR = new Texture("img/abajo_off.png");
		sTXLON = new Texture("img/izquierda_on.png");
		sTXRON = new Texture("img/derecha_on.png");
		sTYLON = new Texture("img/arriba_on.png");
		sTYRON = new Texture("img/abajo_on.png");
		
		////////////////////////////////////////////////////// INIT YOUR GAME HERE
		
		// INIT game graphic stuff
		batch = new SpriteBatch();
		img = new Texture("logo.png");
		font = new BitmapFont();

		// Fancy particle effect!!!
		hpEffect = new ParticleEffect();
		hpEffect.load(Gdx.files.internal("particles/hp_emitter.p"), Gdx.files.internal("img"));
		hpEffect.setPosition(Gdx.graphics.getWidth()/2,Gdx.graphics.getHeight()/2);
		hpEffect.start();
		
		////////////////////////////////////////////////////// INIT YOUR GAME HERE
		
	}

	@Override
	public void render(){
		
		////////////////////////////////////////////////////// RENDER YOUR GAME HERE
		
		float delta = Gdx.graphics.getDeltaTime();
		
		if(gameState == GameState.MENU) {
			
		}else if(gameState == GameState.INIT) {
			
		}else if(gameState == GameState.START) {
			Gdx.gl.glClearColor(0.1f, 0, 0, 1);
			Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
			
			batch.begin();
			batch.draw(img, Gdx.graphics.getWidth()/2 - img.getWidth()/2,Gdx.graphics.getHeight()/2 - img.getHeight()/2);
				hpEffect.draw(batch,delta);
			batch.end();
			
			// SENSOR DATA
			batch.begin();
			if(gameResolver.sensorConnected()){
				if(gameResolver.sensorTXL()){
					batch.draw(sTXLON,8, Gdx.graphics.getHeight()-100);
				}else{
					batch.draw(sTXL,8, Gdx.graphics.getHeight()-100);
				}
				if(gameResolver.sensorTXR()){
					batch.draw(sTXRON,100, Gdx.graphics.getHeight()-100);
				}else{
					batch.draw(sTXR,100, Gdx.graphics.getHeight()-100);
				}
				if(gameResolver.sensorTYL()){
					batch.draw(sTYLON,40, Gdx.graphics.getHeight()-40);
				}else{
					batch.draw(sTYL,40, Gdx.graphics.getHeight()-40);
				}
				if(gameResolver.sensorTYR()){
					batch.draw(sTYRON,40, Gdx.graphics.getHeight()-130);
				}else{
					batch.draw(sTYR,40, Gdx.graphics.getHeight()-130);
				}
				
			}
			batch.end();
			
			if(hpEffect.isComplete()){
				hpEffect.reset();
			}
		}else if(gameState == GameState.UPDATE) {
			
		}else if(gameState == GameState.GAMEOVER) {
			
		}else if(gameState == GameState.PAUSE) {
			
		}else if(gameState == GameState.EXIT) {
			
		}else if(gameState == GameState.WIN) {
			
		}
		
		////////////////////////////////////////////////////// RENDER YOUR GAME HERE
		
	}

	@Override
	public void dispose() {
		batch.dispose();
		hpEffect.dispose();
	}

	@Override
	public void resize(int width, int height) {

	}

	@Override
	public void pause() {
		
	}

	@Override
	public void resume() {
		
	}

	public boolean keyDown(int keycode) {
		switch (keycode) {
		case Input.Keys.BACK:
			gameResolver.openInstructionsActivity();
			Gdx.app.exit();
			return true;
		}
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		gameResolver.initSensorPositionListAdapter();
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		// TODO Auto-generated method stub
		return false;
	}
}
