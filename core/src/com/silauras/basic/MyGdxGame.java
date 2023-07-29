package com.silauras.basic;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.*;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalShadowLight;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.graphics.g3d.utils.DepthShaderProvider;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Vector3;

import java.util.ArrayList;
import java.util.List;

public class MyGdxGame extends ApplicationAdapter {

    public Environment environment;
    public DirectionalShadowLight shadowLight;

    public PerspectiveCamera camera;
    public CameraInputController camController;

    public ModelBatch modelBatch;
    public ModelBatch shadowBatch;
    public Model model;
    public List<ModelInstance> instances = new ArrayList<>();

    @Override
    public void create() {
        createEnvironment();

        createCamera();
        createModel();
    }

    private void createEnvironment() {
        modelBatch = new ModelBatch();
        environment = new Environment();
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, .4f, .4f, .4f, 1f));
        environment.add(new DirectionalLight().set(Color.LIGHT_GRAY, new Vector3( 1f, -.8f, -.2f)));
        environment.add((shadowLight) =
                new DirectionalShadowLight(
                        1024,
                        1024,
                        60f,
                        60f,
                        .1f,
                        50f));
        environment.shadowMap = shadowLight;
        shadowBatch = new ModelBatch(new DepthShaderProvider());
    }

    private void createModel() {
        ModelBuilder modelBuilder = new ModelBuilder();
        model = modelBuilder.createBox(5f, 5f, 5f,
                new Material(ColorAttribute.createDiffuse(Color.BLUE)),
                VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);

        instances.add(new ModelInstance(model));

        instances.add(new ModelInstance(new ModelBuilder()
                .createBox(100, 1, 100,
                        new Material(ColorAttribute.createDiffuse(Color.BROWN)),
                        VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal)));
    }

    private void createCamera() {
        camera = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.position.set(10f, 10f, 10f);
        camera.lookAt(0, 0, 0);
        camera.near = 1f;
        camera.far = 300f;
        camera.update();

        camController = new CameraInputController(camera);
        Gdx.input.setInputProcessor(camController);
    }

    @Override
    public void render() {
        //create shadow texture
        shadowLight.begin();
        shadowBatch.begin(shadowLight.getCamera());

        shadowBatch.render(instances);

        shadowBatch.end();
        shadowLight.end();

        Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        camController.update();

        modelBatch.begin(camera);
        modelBatch.render(instances, environment);
        modelBatch.end();
    }

    @Override
    public void dispose() {
        modelBatch.dispose();
        model.dispose();
    }
}
