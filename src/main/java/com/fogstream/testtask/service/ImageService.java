package com.fogstream.testtask.service;

import com.fogstream.testtask.model.Image;
import com.fogstream.testtask.repository.ImageRepository;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Files;

@Service
public class ImageService
{
	@Value("${storage.images}")
	private String imageStorage;

	private ImageRepository imageRepository;

	public ImageService(ImageRepository imageRepository)
	{
		this.imageRepository = imageRepository;
	}

	private String getImagePath(Image image)
	{
		String filepath = imageStorage + "/" + image.getId() + "." + image.getExtension();
		return filepath;
	}

	public String getImageData(long id) throws Exception
	{
		Image viewingImage = imageRepository.findById(id).orElse(null);
		if (viewingImage == null)
			return null;
		File imageFile = new File(getImagePath(viewingImage));
		if (!imageFile.exists())
			return null;
		return Base64.encodeBase64String(Files.readAllBytes(imageFile.toPath()));
	}

	public Image createImageFrom64BaseString(String imageData, String imageName) throws Exception
	{
		Image image = new Image();
		image.setExtension(imageName.split("\\.")[1]);
		imageRepository.save(image);
		new File(imageStorage).mkdirs();
		String filepath = imageStorage + "/" + image.getId() + "." + image.getExtension();
		File imageFile = new File(filepath);
		imageFile.createNewFile();
		FileOutputStream out = new FileOutputStream(imageFile, false);
		out.write(Base64.decodeBase64(imageData));
		out.close();
		return image;
	}

	public void deleteImage(Image image)
	{
		if (image == null)	return;
		File imageFile = new File(getImagePath(image));
		if (imageFile.exists())	imageFile.delete();
		imageRepository.delete(image);
	}
}
